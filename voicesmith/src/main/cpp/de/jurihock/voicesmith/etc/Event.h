#pragma once

#include <voicesmith/Header.h>

template<typename>
class Event;

template<typename R, typename... Args>
class Event<R(Args...)> final {

public:

  template<typename F>
  Event(F&& action) :
    Event(std::function<R(Args...)>(std::forward<F>(action))) {
  }

  Event(const std::function<R(Args...)> action) :
    actions({action, action}) {
  }

  Event(const Event<R(Args...)>& other) :
    actions(other.actions) {
  }

  void set(const std::function<R(Args...)> action) {
    actions.first = action;
  }

  void reset() {
    actions.first = actions.second;
  }

  R operator()(Args... args) const {
    return actions.first(args...);
  }

private:

  struct {
    std::function<R(Args...)> first;
    std::function<R(Args...)> second;
  } actions;

};
