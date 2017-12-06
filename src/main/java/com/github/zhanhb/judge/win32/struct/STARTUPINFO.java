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
 * https://msdn.microsoft.com/zh-tw/library/windows/desktop/ms686331(v=vs.85).aspx
 */
public class STARTUPINFO extends jnr.ffi.Struct {

    private final DWORD cb = new DWORD();
    private final uintptr_t /*LPTSTR*/ lpReserved = new uintptr_t(); //new UTF8String();
    private final uintptr_t /*LPTSTR*/ lpDesktop = new uintptr_t(); //UTF8String();
    private final uintptr_t /*LPTSTR*/ lpTitle = new uintptr_t(); //new UTF8String();
    private final DWORD dwX = new DWORD();
    private final DWORD dwY = new DWORD();
    private final DWORD dwXSize = new DWORD();
    private final DWORD dwYSize = new DWORD();
    private final DWORD dwXCountChars = new DWORD();
    private final DWORD dwYCountChars = new DWORD();
    private final DWORD dwFillAttribute = new DWORD();
    private final DWORD dwFlags = new DWORD();
    private final WORD wShowWindow = new WORD();
    private final WORD cbReserved2 = new WORD();
    private final uintptr_t /*LPBYTE*/ lpReserved2 = new uintptr_t();
    private final uintptr_t /*HANDLE*/ hStdInput = new uintptr_t();
    private final uintptr_t /*HANDLE*/ hStdOutput = new uintptr_t();
    private final uintptr_t /*HANDLE*/ hStdError = new uintptr_t();

    @SuppressWarnings("LeakingThisInConstructor")
    public STARTUPINFO(jnr.ffi.Runtime runtime) {
        super(runtime);
        cb.set(sizeof(this));
    }

    public void setFlags(int flags) {
        dwFlags.set(flags);
    }

    public void setStandardInput(long standardInput) {
        hStdInput.set(standardInput);
    }

    public void setStandardOutput(long standardOutput) {
        hStdOutput.set(standardOutput);
    }

    public void setStandardError(long standardError) {
        hStdError.set(standardError);
    }

}
