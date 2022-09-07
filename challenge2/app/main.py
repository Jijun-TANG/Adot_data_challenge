from typing import Union
import psycopg2
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()


class Item(BaseModel):
    name: str
    price: float
    is_offer: Union[bool, None] = None


try:
    connection = psycopg2.connect(user="admin",
                                  password=1234,
                                  host="127.0.0.1",  # or localhost
                                  port="15432",
                                  database="postgis")
except (Exception, psycopg2.Error) as error:
    print("Failed to establish connection to postgis database:\n\n", error)


@app.get("/parcelle/{lat}/{lon}")
async def get_nearest_parcel(lat: float = 48.8, lon: float = 2.3):
    """
    https://stackoverflow.com/questions/37827468/find-the-nearest-location-by-latitude-and-longitude-in-postgresql
    https://gis.stackexchange.com/questions/86079/distance-between-polygon-and-point
    Return the information of the neareast parcel given a location point
    """
    pass


@app.get("/parcelle/")
@app.get("/")
def read_root():
    return {"Hello": "World"}


@app.delete("/parcelle/")
@app.get("/items/{item_id}")
def read_item(item_id: int, q: Union[str, None] = None):
    return {"item_id": item_id, "q": q}


@app.put("/items/{item_id}")
def update_item(item_id: int, item: Item):
    return {"item_name": item.name, "item_id": item_id}
