## Procedures to make this work:

1. Go to the directory where `docker-compose.yml` is stored. Launch docker and type
```
docker compose up
```
2. Go to `localhost:5050/browser/` and login by username `admin@admin.com` and password `root`.
Right click on `Servers`, choose `Register` -> `Server` and pick a database name you like, for example, `postgis`.
![](images_of_introduction/Pasted%20image%2020220907130302.png)

3. Open another terminal and type `docker ps`, find the CONTAINER ID of the `postgis`, copy it \[ContainerID\], and type `docker inspect [ContainerID]`
![](images_of_introduction/Pasted%20image%2020220907130401.png)

4. paste the ip address found in the connection tab:

![](images_of_introduction/Pasted%20image%2020220907130641.png)

5. Launch `jupyter notebook` in the designated directory with conda Promp and click on `kernel` -> `Restart & Run All`
![](images_of_introduction/Pasted%20image%2020220907133035.png)