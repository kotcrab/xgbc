# xgbc

Game Boy emulator written in Kotlin using libGDX and [VisUI](https://github.com/kotcrab/vis-editor/wiki/VisUI)

#### Status
* Working cpu emulation 
  * `cpu_instrs`,  `instr_timing` and `mem_timing` tests are passing. 
* Debugger with 'Run to Line' function and breakpoints
* VRAM viewer and initial GPU emulation (tilemap rendering)
* MBC1 controller, Serial port, Joypad, Timer, DIV emulation implemented

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

01:ok  02:ok  03:ok  

Passed
```

Running Tetris (without sprites support)
![Tetris Screenshot](http://dl.kotcrab.com/img/d/2017-02-25_2014.png)

VRAM viewer (showing first tile palette) and debugger:  
![VRAM Screenshot](http://dl.kotcrab.com/img/d/2016-04-20_2341.png)
