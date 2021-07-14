# Asperge
Asperge is an open-source implementation of Sketchware's code generator

## Building
Building the jar is very simple. First, you would need a pc with gradle installed in it and run
```console
$ ./gradlew jar
```
> For windows users:
> ```
> .\gradlew.bat jar
> ```

After the command has finished executing, you would see a `build` folder appear on the project directory. Open it, go to the `libs` folder, and `asperge-1.0.jar` is the compiled jar file of asperge, Have fun with it!

## Why?
So you can generate codes from sketchware projects right on your pc, and you won't need sketchware for that. + because this project is open source, you can tweak it as you like! (for the most part, it's just for fun and learning experience)

### TODOs
 - [x] Barebone of blocks code generation
 - [x] Barebone of XML layout generation
 - [x] Command Line Interface
 - [x] Nested blocks
 - [x] Implement moreblocks
 - [x] AndroidManifest.xml generation (partially)
 - [ ] Implement components
 - [ ] `parse` command
 - [ ] Tedious: Expand the keys of XML layout generation
 - [ ] Tedious: Need more opcodes
 - [ ] Tedious: Need more events
