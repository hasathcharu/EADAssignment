# EAD Assignment

### Build Steps

Choose the platform in pom.xml by uncommenting the relevant lines.

```xml
<platforms>
	<!--<platform>-->
		<!--<architecture>amd64</architecture>-->
		<!--<os>linux</os>-->
	<!--</platform>-->
	<platform>
		<architecture>arm64</architecture>
	  	<os>linux</os>
	</platform>
</platforms>
```

### Load the Maven Project

### Run the build command

```bash
mvn compile jib:dockerBuild
```

### Create the secrets file `secrets.txt` and define the following Environment Variables

```
INVENTORY_DB=
IDENTITY_DB=
ORDER_DB=
USER_DB=
```

### Create the secrets file `jwtsecret.txt` and define the following Environment Variables

```
JWT_SECRET=
```

### Run Docker-Compose

```
docker-compose up
```
