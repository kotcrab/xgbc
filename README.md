# xgbc

Game Boy emulator written in Kotlin using libGDX and [VisUI](https://github.com/kotcrab/vis-editor/wiki/VisUI)

#### Status
* Working cpu emulation (`cpu_instrs` and `instr_timing` passing. `mem_timing`: 'read', 'write' tests pass, some 'modify' tests fail)
* Debugger with 'Run to Line' function and breakpoints

Test roms:
```
cpu_instrs

01:ok  02:ok  03:ok  04:ok  05:ok  06:ok  07:ok  08:ok  09:ok  10:ok  11:ok  

Passed all tests
```

```
instr_timing


Passed
```

```
mem_timing

01:ok  02:ok  03:01  

Failed 1 tests.
```

VRAM viewer (showing first tile palette) and debugger:  
![Screenshot](http://dl.kotcrab.com/img/d/2016-04-20_2341.png)
