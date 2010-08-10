from transitfeed import Loader
import sys

if len(sys.argv) < 3:
  print "usage: script.py gtfs_filename database_filename"
  exit()

gtfs_filename = sys.argv[1]
database_filename = sys.argv[2]

loader = Loader( gtfs_filename, memory_db=False, db_filename=database_filename )
sched = loader.Load(verbose=True)
