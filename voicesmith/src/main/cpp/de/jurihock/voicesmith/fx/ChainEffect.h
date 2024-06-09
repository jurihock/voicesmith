#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/fx/AudioEffect.h>

template<typename... AudioEffects>
class ChainEffect final : public AudioEffect {

public:

  static_assert(
    (std::is_base_of_v<AudioEffect, AudioEffects>&&...),
    "Invalid audio effect type!");

  static_assert(
    (sizeof...(AudioEffects)),
    "Provide at least one audio effect!");

  ChainEffect() :
    ChainEffect(std::make_shared<AudioEffects>()...) {}

  ChainEffect(std::shared_ptr<AudioEffects>... effects) :
    effects(std::make_tuple(effects...)) {}

  template<size_t Index>
  inline auto fx() const { return std::get<Index>(effects); }

  template<typename Type>
  inline auto fx() const { return std::get<std::shared_ptr<Type>>(effects); }

  void reset(const float samplerate, const size_t blocksize) override {
    auto onreset = [&](auto& effect){ effect->reset(samplerate, blocksize); };
    std::apply([onreset](auto&... effect){(..., onreset(effect));}, effects);

    auto onresize = [&](auto& buffer){ buffer.resize(blocksize); };
    std::apply([onresize](auto&... buffer){(..., onresize(buffer));}, buffers);

    auto onfill = [&](auto& buffer){ std::fill(buffer.begin(), buffer.end(), 0); };
    std::apply([onfill](auto&... buffer){(..., onfill(buffer));}, buffers);
  }

  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override {
    auto enumerate = []<typename... T>(const std::tuple<T...>& values) {
      return [&]<std::size_t... index>(std::index_sequence<index...>) {
        return std::make_tuple(std::make_pair(index, std::get<index>(values))...);
      }(std::make_index_sequence<sizeof...(T)>());
    };

    auto onapply = [&](auto& pair) {
      auto effect = pair.second;
      auto total = sizeof...(AudioEffects);

      auto i = pair.first;
      auto j = int(total) - 1;

      auto x = i % 2;
      auto y = x ^ 1;

      auto src = (i > 0) ? buffers[x] : input;
      auto dst = (i < j) ? buffers[y] : output;

      effect->apply(index, src, dst);
    };

    auto pairs = enumerate(effects);
    std::apply([onapply](auto&... pair){(..., onapply(pair));}, pairs);
  }

private:

  const std::tuple<std::shared_ptr<AudioEffects>...> effects;
  std::array<std::vector<float>, 2> buffers;

};
