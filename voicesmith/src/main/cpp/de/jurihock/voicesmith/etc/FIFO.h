#pragma once

#include <voicesmith/Header.h>

#include <readerwriterqueue.h>

template<typename T>
class FIFO {

public:

  FIFO() {
    resize(0, [](size_t) { return nullptr; }, [](T*) {});
  }

  virtual ~FIFO() {
    clear();
  }

  void clear() {
    T* value;

    while (done.try_dequeue(value)) {
      func.free(value);
    }

    while (todo.try_dequeue(value)) {
      func.free(value);
    }
  }

  void resize(const size_t size) {
    clear();

    todo = moodycamel::BlockingReaderWriterQueue<T*>(size);
    done = moodycamel::BlockingReaderWriterQueue<T*>(size);

    for (size_t i = 0; i < size; ++i) {
      done.enqueue(func.alloc());
    }
  }

  void resize(const size_t size, std::function<T*(size_t)> alloc, std::function<void(T*)> free) {
    clear();

    func = {alloc, free};
    todo = moodycamel::BlockingReaderWriterQueue<T*>(size);
    done = moodycamel::BlockingReaderWriterQueue<T*>(size);

    for (size_t i = 0; i < size; ++i) {
      done.enqueue(func.alloc(i));
    }
  }

  bool empty() const {
    return todo.peek() == nullptr;
  }

  void flush() {
    T* value;

    while (todo.try_dequeue(value)) {
      done.enqueue(value);
    }
  }

  bool write(std::function<void(T& value)> callback) {
    T* value;

    if (!done.try_dequeue(value)) {
      return false;
    }

    callback(*value);
    todo.enqueue(value);
    return true;
  }

  template<typename R, typename P>
  bool write(const std::chrono::duration<R, P>& timeout, std::function<void(T& value)> callback) {
    T* value;

    if (!done.wait_dequeue_timed(value, timeout)) {
      return false;
    }

    callback(*value);
    todo.enqueue(value);
    return true;
  }

  bool read(std::function<void(T& value)> callback) {
    T* value;

    if (!todo.try_dequeue(value)) {
      return false;
    }

    callback(*value);
    done.enqueue(value);
    return true;
  }

  template<typename R, typename P>
  bool read(const std::chrono::duration<R, P>& timeout, std::function<void(T& value)> callback) {
    T* value;

    if (!todo.wait_dequeue_timed(value, timeout)) {
      return false;
    }

    callback(*value);
    done.enqueue(value);
    return true;
  }

private:

  struct {
    std::function<T*(size_t)> alloc;
    std::function<void(T*)> free;
  } func;

  moodycamel::BlockingReaderWriterQueue<T*> todo;
  moodycamel::BlockingReaderWriterQueue<T*> done;

};
