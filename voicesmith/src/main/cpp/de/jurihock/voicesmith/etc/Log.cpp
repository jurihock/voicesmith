#include <voicesmith/etc/Log.h>

#include <android/log.h>

INITIALIZE_EASYLOGGINGPP

class AndroidLogHandler final : public el::LogDispatchCallback {

public:

  void handle(const el::LogDispatchData* data) {
    static const auto tag = "voicesmith.cpp";
    const auto msg = data->logMessage()->message().c_str();

    const el::Level src = data->logMessage()->level();
    std::optional<int> dst = std::nullopt;

    switch (src) {
      case el::Level::Verbose:
        dst = ANDROID_LOG_VERBOSE;
        break;
      case el::Level::Debug:
        dst = ANDROID_LOG_DEBUG;
        break;
      case el::Level::Info:
        dst = ANDROID_LOG_INFO;
        break;
      case el::Level::Warning:
        dst = ANDROID_LOG_WARN;
        break;
      case el::Level::Error:
        dst = ANDROID_LOG_ERROR;
        break;
      case el::Level::Fatal:
        dst = ANDROID_LOG_FATAL;
        break;
      default:
        break;
    }

    if (dst) {
      __android_log_print(dst.value(), tag, "%s", msg);
    }
  }

  static bool install() {
    el::Helpers::uninstallLogDispatchCallback<el::base::DefaultLogDispatchCallback>("DefaultLogDispatchCallback");
    return el::Helpers::installLogDispatchCallback<AndroidLogHandler>("AndroidLogHandler");
  }

};

static bool IsAndroidLogHandlerInstalled = AndroidLogHandler::install();
