#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/fx/AudioEffect.h>

template<typename... AudioEffects>
class ChainEffect final : public AudioEffect {

public:

  static_assert(
    (sizeof...(AudioEffects)),
    "Provide at least one audio effect!");

  static_assert(
    (std::is_base_of_v<AudioEffect, AudioEffects>&&...),
    "Invalid audio effect type!");

  ChainEffect() :
    ChainEffect(std::make_shared<AudioEffects>()...) {}

  explicit ChainEffect(const std::shared_ptr<AudioEffects>&&... effects) :
    effects(std::make_tuple(effects...)) {}

  // TODO function callback
  // template<size_t Index>
  // inline auto get() const { return std::get<Index>(effects); }

  template<typename Type>
  inline void get(const std::function<void(std::shared_ptr<Type> effect)> callback) const
  {
    callback(std::get<std::shared_ptr<Type>>(effects));
  }

  void reset(const float samplerate, const size_t blocksize) override {
    for_each_invoke(effects, [&](auto&& effect){
      effect->reset(samplerate, blocksize);
    });
    for_each_invoke(buffers, [&](auto&& buffer){
      buffer.resize(blocksize);
      std::fill(buffer.begin(), buffer.end(), 0);
    });
  }

  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override {
    for_each_invoke(enumerate(effects), [&](auto&& keyval){
      auto effect = keyval.second;
      auto total = sizeof...(AudioEffects);

      auto i = keyval.first;
      auto j = int(total) - 1;

      auto x = i % 2;
      auto y = x ^ 1;

      auto src = (i > 0) ? buffers[x] : input;
      auto dst = (i < j) ? buffers[y] : output;

      effect->apply(index, src, dst);
    });
  }

private:

  const std::tuple<std::shared_ptr<AudioEffects>...> effects;
  std::array<std::vector<float>, 2> buffers;

  template<typename T, typename F>
  inline static void for_each_invoke(T&& values, F&& f) {
    [&]<std::size_t... index>(std::index_sequence<index...>) {
      (..., f(std::get<index>(values)));
    }(std::make_index_sequence<std::tuple_size_v<std::decay_t<T>>>());
  }

  template<typename T>
  inline static auto enumerate(T&& values) {
    return [&]<std::size_t... index>(std::index_sequence<index...>) {
      return std::make_tuple(std::make_pair(index, std::get<index>(values))...);
    }(std::make_index_sequence<std::tuple_size_v<std::decay_t<T>>>());
  }

};
