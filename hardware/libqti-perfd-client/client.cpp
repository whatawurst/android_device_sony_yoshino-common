#include <stdint.h>

extern "C" {
    void perf_get_feedback() {}
    int perf_hint(int /*hint*/, const char* /*pkg*/, int /*duration*/, int /*type*/) {
        return 233;
    }
    int perf_lock_acq(int handle, int /*duration*/, int* /*hints*/, int /*num_args*/) {
        if (handle > 0)
            return handle;
        return 233;
    }
    void perf_lock_cmd() {}
    int perf_lock_rel(int handle) {
        if (handle > 0)
            return handle;
        return 233;
    }
    void perf_lock_use_profile() {}
}
