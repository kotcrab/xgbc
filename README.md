# xgbc

Trying to make Game Boy emulator

#### Status
* Half working cpu emulation
* Debugger with 'Run to Line' function, breakpoints not ready

Test roms:
```
cpu_instrs

01:ok  02:ok  03:ok  04:ok  05:ok  06:ok  07:ok  08:ok  09:ok  10:ok  11:ok [starts looping, should not happen]
```
Doesn't seem to be trustworthy running test individually can give different results. 07 doesn't finish, fault in 09 and 11
