# sorting_natural_merging
Implementation of sorting sequential file using natural merge in a 2 + 2 scheme.
The application provides block access to the disk, block size is set by user.
After sorting data is left on Tape file.
Program alows to:
- generate file with the given number of records,
- importing data for sorting from a file,
- entering records from the keyboard,
- changing the size of the cached data block (pages),
- setting to display data from tapes after each operation,
- sorting the file with the merge method based on the 2 + 2 variant.

### Record
A class - single record in a sequential file.
Each record has 5 floating point numbers.
The data to the constructor is stored as an array of 5 float numbers.
Records in files are stored in binary format (each record takes 20 bytes).
It has a getKey() function that calculates and returns the key value in the float format.

### IOLayer
The class responsible for accessing the disk. Provides the main program layer with write and
read block operations to / from disk memory.

### Tape
The Tape class represents both the tapes used for sorting and the file from which we are reading
data to be sorted. Contains two buffers with the maximum size of pageSize for storage
successively read from the disk and those for writing. 
Each tape has a name which is also the name of the corresponding file
disk.

### Sorting
#### I Phase (Distribution on tapes)

The loop fetches a record from the input file (up to
last record). The first goes to the first tape. Each
the next is compared with its predecessor and follows either
changing the tape and writing at the end or is added after
predecessor on the current tape (after adding it becomes
predecessor). Buffers on both tapes are flushed.
The first element of the second belt is checked, if not
exists, it means that the sorted data is on
the first tape.

#### II Phase (Merging tapes together)

The data from the distributed tapes is alternating
compared with each other (and within oneself in order to detect
end of series [series may stick together here, no
formerly]). The data to be compared are listed
for two more, previously cleaned tapes. Change
tape for recording occurs when the batch records are exhausted
both from one and the other tape (odd series
they land on the first tape and even on the second).
The process ends when the last item is on the tapes
will be taken from them. These activities are carried out in a loop
which ends when it was not needed after the next merge
the second of the tapes. The sorted data appears in the first of
tapes (on which the compared data was written).
