# Reduced MIPS Assembler
This is a program I created for my Computer Architecture course during my junior year at the University of Wisconsin - Milwaukee. This program is meant to partially simulate the functioning of a MIPS assembly. 
# What does it do?
Basically, the program will take an line of assembly code from the command line of a terminal and give an hexadecimal output. This output represents the assembly instruction in hexadecimal.
The program will assume all input is valid input in its current state. The program is able to handle up to 15 MIPS assembly instructions:
- add
- addiu
- and
- andi
- beq
- bne
- j
- lui
- lw
- or
- ori
- slt
- sub
- sw
- syscall

# How to get it to work?
It is recommended you use Intellij to run this program. However, other IDE should be able to run this program as well.
Once you get terminal opened and are in the same folder as the jar file for the program (once you build it), you can input a line like this: <br />

### FOR WINDOWS:
WARNING: Replace "Name of jar" with the name of the jar file used on your IDE.
```
java -jar < Name of jar >.jar "sub `$t5, `$s1, `$s2"
```
In Windows, when writing registers, you need to put "`" before it, since "$" are seen as special characters. <br />

### FOR LINUX/MAC:
```
$> java -jar < Name of jar >.jar "add \$t5, \$s7, \$5"
```
In Linux/Mac, you precede the dollar sign with a backslash.
