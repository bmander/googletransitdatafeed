import problems as problems_module

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

  def GetSqlValuesTuple(self, **extra_fields):
    """Return a tuple that outputs a row of _FIELD_NAMES to be written to a
       SQLite database.

    Arguments:
        extra_fields: a dictionary of fields that you would like to add or
                      override when constructing the tuple. For example, 
                      the StopTime class does not have an attribute 'trip_id'
                      but it is nevertheless a SQL field - it would be prudent
                      to include it in extra_fields.
    """

    result = []
    for fn, ftype in self._SQL_FIELDS:
      if fn in extra_fields:
        result.append(extra_fields[fn])
      else:
        # Since we'll be writting to SQLite, we want empty values to be
        # outputted as NULL string (contrary to what happens in
        # GetFieldValuesTuple)
        result.append(getattr(self, fn))
    return tuple(result)

  def save(self, cursor, **extra_fields):
    
    insert_query = "INSERT INTO stop_times (%s) VALUES (%s);" % (
       ','.join([fn for fn, ft in self._SQL_FIELDS]),
       ','.join(['?'] * len(self._SQL_FIELDS)))

    cursor.execute( 
        insert_query, self.GetSqlValuesTuple(**extra_fields))
  
  @classmethod
  def delete( cls, cursor, **fields ):
    where_clause = " and ".join( ["%s=?"%k for k in fields.keys()] )
    query = "DELETE FROM stop_times WHERE "+where_clause
    cursor.execute( query, fields.values() )

    # right now, this should break. where's my unit tests at?
    if cursor.rowcount == 0:
      raise problems_module.Error, 'Attempted deletion of object which does not exist'
