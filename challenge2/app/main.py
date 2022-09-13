import json
from typing import Union, Dict, List
import psycopg2
from fastapi import FastAPI, Response, status
from pydantic import BaseModel

app = FastAPI()


class Item(BaseModel):
    name: str
    price: float
    is_offer: Union[bool, None] = None


class Geometry(BaseModel):
    type: str
    coordinates: List[List[List[float]]]


class Parcelle(BaseModel):
    Id: str
    Commune: str
    Geometry: Geometry


class UpdateParcelle(BaseModel):
    Id: str
    Commune: Union[str, None] = None
    Geometry: Union[Geometry, None] = None


try:
    connection = psycopg2.connect(user="admin",
                                  password=1234,
                                  host="127.0.0.1",  # or localhost
                                  port="15432",
                                  database="postgis")
except (Exception, psycopg2.Error) as error:
    print("Failed to establish connection to postgis database:\n\n", error)


# 48.853578021375, 2.348097181051865 point zéro
@app.get("/parcelle/{lat}/{lon}")
async def get_nearest_parcel(lat: float = 48.853578021375, lon: float = 2.348097181051865):
    """
    https://stackoverflow.com/questions/37827468/find-the-nearest-location-by-latitude-and-longitude-in-postgresql
    https://gis.stackexchange.com/questions/86079/distance-between-polygon-and-point
    Return the information of the neareast parcel given a location point
    """
    cursor = connection.cursor()
    sql_select_query4 = """select c.id, c.commune, ST_WITHIN(ST_MakePoint(%s,%s), c.geometry) as dist from cadastre c order by dist desc limit 1"""
    cursor.execute(sql_select_query4, (lon, lat))
    query4_result = cursor.fetchone()
    if query4_result[2]:  # If we found a polygon which contains the point with 'WITHIN' command
        cursor.close()
        return (query4_result[0], query4_result[1])
    else:  # We use the built in minimum distance calculator to find the neareast parcel
        sql_select_query5 = """select c.id, c.commune, ST_Distance(ST_MakePoint(%s, %s), ST_Centroid(c.geometry)) as dist from cadastre c order by dist ASC limit 1;"""
        cursor.execute(sql_select_query5, (lon, lat))
        query5_result = cursor.fetchone()
        cursor.close()
        return (query5_result[0], query5_result[1])


@app.post("/parcelle/{parcel.Id}")
async def insert_parcel(parcel: Parcelle):
    cursor = connection.cursor()
    sql_select_query = f"select id from cadastre c where c.id='{parcel.Id}';"
    cursor.execute(sql_select_query)
    id_list = cursor.fetchall()
    if len(id_list) > 0:
        # We have already a parcel with the same id in database
        return Response(status_code=status.HTTP_204_NO_CONTENT)

    chains = str()
    for j in range(len(parcel.Geometry.coordinates)):
        chains += '('
        for coords in parcel.Geometry.coordinates[j]:
            chains += str(coords[0]) + ' ' + str(coords[1]) + ', '
        chains = chains[:-2]
        chains += '),'
    chains = chains[:-1]

    geo_str = parcel.Geometry.type.upper()+'('+chains+')'
    postgres_insert_query = """ INSERT INTO cadastre (ID, Commune, Geometry) VALUES ( %s, %s, %s)"""
    try:
        cursor.execute(postgres_insert_query,
                       (parcel.Id, parcel.Commune, geo_str))
    except:
        return Response(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR)
    connection.commit()
    cursor.close()
    return Response(content="{Id} parcel is successfully created", status_code=status.HTTP_201_CREATED)


@app.delete("/parcelle/")
async def delete_parcel(Id: str):
    cursor = connection.cursor()
    sql_select_query = f"select id from cadastre c where c.id='{Id}';"
    cursor.execute(sql_select_query)
    id_list = cursor.fetchall()
    if len(id_list) == 0:
        # We have already a parcel with the same id in database
        return Response(status_code=status.HTTP_404_NOT_FOUND)
    postgres_delete_query = f"DELETE FROM cadastre c WHERE c.id ='{Id}';"
    try:
        cursor.execute(postgres_delete_query)
    except:
        return Response(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR)
    connection.commit()
    cursor.close()
    return Response(content="{Id} parcel is successfully deleted", status_code=status.HTTP_202_ACCEPTED)


@app.patch("/parcelle/{ParcelToUpdate.Id}")
async def update_item(ParcelToUpdate: UpdateParcelle):
    cursor = connection.cursor()
    sql_select_query = f"select id from cadastre c where c.id='{ParcelToUpdate.Id}';"
    cursor.execute(sql_select_query)
    id_list = cursor.fetchall()
    if len(id_list) == 0:
        # We have already a parcel with the same id in database
        return Response(content="parcel {ParcelToUpdate.Id} not found", status_code=status.HTTP_404_NOT_FOUND)
    if ParcelToUpdate.Commune == None and ParcelToUpdate.Geometry == None:
        """
        No update information provided
        """
        return Response(content="Nothing to update for parcel {ParcelToUpdate.Id}", status_code=status.HTTP_204_NO_CONTENT)
    if ParcelToUpdate.Commune != None and ParcelToUpdate.Geometry == None:
        """
        Only need to update commune info
        """
        postgres_update_query = f"UPDATE cadastre SET Commune='{ParcelToUpdate.Commune}' WHERE id ='{ParcelToUpdate.Id}';"
    if ParcelToUpdate.Geometry != None:
        """
        Geometry info need to be updated
        """
        chains = str()
        for j in range(len(ParcelToUpdate.Geometry.coordinates)):
            chains += '('
            for coords in ParcelToUpdate.Geometry.coordinates[j]:
                chains += str(coords[0]) + ' ' + str(coords[1]) + ', '
            chains = chains[:-2]
            chains += '),'
        chains = chains[:-1]
        geo_str = ParcelToUpdate.Geometry.type.upper()+'('+chains+')'
        if ParcelToUpdate.Commune == None:
            postgres_update_query = f"UPDATE cadastre SET Geometry='{geo_str}' WHERE Id ='{ParcelToUpdate.Id}';"
        else:
            postgres_update_query = f"UPDATE cadastre SET Commune='{ParcelToUpdate.Commune}', Geometry='{geo_str}' WHERE id ='{ParcelToUpdate.Id}';"
    cursor.execute(postgres_update_query)
    try:
        cursor.execute(postgres_update_query)
    except:
        return Response(content="Update action failed", status_code=status.HTTP_500_INTERNAL_SERVER_ERROR)
    connection.commit()
    cursor.close()
    return Response(content="{ParcelToUpdate.Id} parcel is successfully updated", status_code=status.HTTP_202_ACCEPTED)
"""
Code examples:

@app.get("/items/{item_id}")
def read_item(item_id: int, q: Union[str, None] = None):
    return {"item_id": item_id, "q": q}


@app.put("/items/{item_id}")
def update_item(item_id: int, item: Item):
    return {"item_name": item.name, "item_id": item_id}


@app.patch("/items/{item_id}", response_model=Item)
async def update_item(item_id: str, item: Item):
    stored_item_data = items[item_id]
    stored_item_model = Item(**stored_item_data)
    update_data = item.dict(exclude_unset=True)
    updated_item = stored_item_model.copy(update=update_data)
    items[item_id] = jsonable_encoder(updated_item)
    return updated_item

"""
