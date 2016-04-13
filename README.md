# xgbc

Trying to make Game Boy emulator

#### Status
* Half working cpu emulation
* Debugger with 'Run to Line' function, breakpoints not ready

Test roms:
```
cpu_instrs

01:ok  02:ok  03:ok  04:ok  05:ok  06:ok  07:ok  08:ok  09:01  10:ok  11:01  

Failed 2 tests.
```
~~Doesn't seem to be trustworthy running test individually can give different results.~~ Never mind, MBC1 controller was not fully
implemented.
