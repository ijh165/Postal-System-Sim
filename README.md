# Postal-System-Sim
CMPT213 assignment 2 and assignment 4. This program simulates the global postal system through a series of file I/O. This program reads 3 input files and generates an "Output" folder containing several log files. PostalSystem1.0 is the first iteration of the postal system (assignment 2) and PostalSystem2.0 is the second iteration of the postal system (assignment 4). There will soon be the third iteration of this project that includes a GUI.

# Input Files
- The program will search for these files in the project base directory
- "commands.txt" contains a list of commands specifying what the program should do next
- "offices.txt" contains a list of post offices and their information
- "wanted.txt" contains a list of wanted criminals (supposedly trying to take over the world by taking advantage of this postal system)

# Output Files
- The program will generate the "Output" folder containing these files
- "log_master.txt" is the master log containing end of day entries, bribery detected entries and letter/package destroyed entries
- "log_front.txt" contains criminal apprehended entries (which is only written when a criminal is apprehended)
- "log_X.txt" (where X is a post office name) are post office logs generated for each post offices and these log files contain end of day entries, letter/package entries and transit entries
