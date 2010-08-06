#!/usr/bin/python2.5

# Copyright (C) The transitfeed authors 
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from persistable import Persistable

class ServicePeriodException( Persistable ):

  _FIELD_NAMES = ['service_id', 'date', 'exception_type']
  
  _SQL_TABLENAME = "calendar_dates"
  _SQL_FIELD_TYPES = ["CHAR(50)", "CHAR(8)", "INTEGER"]
  _SQL_FIELDS = zip( _FIELD_NAMES, _SQL_FIELD_TYPES )

  def __init__(self, service_id, date, exception_type):
    self.service_id = service_id;
    self.date = date;
    self.exception_type = exception_type
