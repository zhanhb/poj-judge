/*
 * Copyright 2017 ZJNU ACM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.zhanhb.judge.win32.struct;

import static com.github.zhanhb.judge.win32.Native.sizeof;

/**
 * @see
 * https://msdn.microsoft.com/en-us/library/windows/desktop/aa379560(v=vs.85).aspx
 */
public class SECURITY_ATTRIBUTES extends jnr.ffi.Struct {

    private final DWORD nLength = new DWORD();
    private final uintptr_t /*LPVOID*/ lpSecurityDescriptor = new uintptr_t();
    private final WBOOL bInheritHandle = new WBOOL();

    @SuppressWarnings("LeakingThisInConstructor")
    public SECURITY_ATTRIBUTES(jnr.ffi.Runtime runtime) {
        super(runtime);
        nLength.set(sizeof(this));
    }

    public void setInheritHandle(boolean inheritable) {
        bInheritHandle.set(inheritable);
    }

}
