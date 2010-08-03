class Persistable:

  @classmethod
  def create_table(self, cursor):
    fields_spec = ",".join(["%s %s"%(fn,ftype) for fn,ftype in self._SQL_FIELDS])
    cursor.execute("""CREATE TABLE %s (%s);"""%(self._SQL_TABLENAME,
                                                    fields_spec))
  @classmethod
  def create_indices(self, cursor):  
    for fn in self._SQL_INDEXABLE_FIELDS:
      cursor.execute("""CREATE INDEX %s_index ON %s (%s);"""%(fn,
                                                              self._SQL_TABLENAME,
                                                              fn))
