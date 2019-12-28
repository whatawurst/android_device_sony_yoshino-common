//
// Copyright (C) 2019 CarbonROM
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package yoshino

import (
    "android/soong/android"
    "android/soong/cc"
    "strings"
)

func initLibs(ctx android.BaseContext) []string {
    var libs []string

    var config = ctx.AConfig().VendorConfig("SONY_YOSHINO_INIT")
    var extension = strings.TrimSpace(config.String("EXTENSION"))

    if len(extension) > 0 {
        libs = append(libs, extension)
    }
    return libs
}

func initLibrary(ctx android.LoadHookContext) {
    type props struct {
        Target struct {
            Android struct {
                Whole_static_libs []string
            }
        }
    }

    p := &props{}
    p.Target.Android.Whole_static_libs = initLibs(ctx)
    ctx.AppendProperties(p)
}

func initLibraryFactory() android.Module {
    module, library := cc.NewLibrary(android.HostAndDeviceSupported)
    library.BuildOnlyStatic()
    newMod := module.Init()
    android.AddLoadHook(newMod, initLibrary)
    return newMod
}
