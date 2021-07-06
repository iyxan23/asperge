# Substack system
Sketchware's block system of storing nested elements is quite interesting. It uses two values, `subStack1` and `subStack2` used to identify which blocks are the start / end of the nested element.

`subStack1` stores the block id where the first block of the first nest is located

`subStack2` stores the block id where the first block of the second nest is located

> Yes, you cannot have nests for more than 2 in sketchware, sadly

### Example(s):
#### If block
```
ID SS1 SS2  OPCODE

1:  2, -1   if
2: -1, -1   exampleOpCode1
3: -1, -1   exampleOpCode2
```

Results in:
 - If nest
   - exampleOpCode1
   - exampleOpCode2

#### If Else block
```
ID SS1 SS2  OPCODE

1:  2,  4   ifElse
2: -1, -1   exampleOpCode1
3: -1, -1   exampleOpCode2
4: -1, -1   exampleOpCode3
```

Results in:
 - If nest
   - exampleOpCode1
   - exampleOpCode2
 - Else nest
   - exampleOpCode3
