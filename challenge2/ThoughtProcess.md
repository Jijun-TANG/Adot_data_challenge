# Mission:
## Implement an API able to communicate with server stocking the geospatial data
<ul>
<li>A GET method returns the nearest parcel based on the location provided</li>
<li>A POST method which insert a new parcel in the database</li>
<li>A DELETE method that deletes the parcel in the database</li>
<li>A PATCH method which allows us to modify one or more columns of a parcel in the databases</li>
</ul>

## Set up python virtual environment (already exisited in the path) and install the necessary libraries
```
python -m venv .venv

.venv\scripts\python.exe -m pip install fastapi

.venv\scripts\python.exe -m pip install "uvicorn[standard]"

.venv\scripts\python.exe -m pip install psycopg2
```

## To launch the program:

### 1. Activate the virtual environment: 
```
.venv\Scripts\activate.bat
```