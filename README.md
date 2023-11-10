# EAD Assignment

## Build Steps

### Choose the Platform

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

## Available Endpoints

| Service              | Endpoint                                         | Method | Description                                             | Role      |
|----------------------|--------------------------------------------------|--------|---------------------------------------------------------|-----------|
| Identity Management  | `/api/auth/public/authenticate`                  | POST   | Login                                                   | Public    |
|                      | `/api/auth/public/register`                      | POST   | Register                                                | Public    |
|                      | `/api/auth/public/authorize`                     | GET    | The authorize endpoint to validate JWT                  | Public    |
|                      | `/api/auth/basic/change-password`                | PUT    | Change password                                         | Basic     |
|                      | `/api/auth/system/{email}`                       | DELETE | Delete User                                             | System    |
|                      | `/api/auth/admin/assign-role`                    | PUT    | Assign a role to a user                                 | Admin     |
|                      | `/api/auth/admin/remove-role`                    | PUT    | Remove a role from user                                 | Admin     |
| Inventory Management | `/api/inventory/public`                          | GET    | Get all inventory                                       | Public    |
|                      | `/api/inventory/public/{PID}`                    | GET    | Get one item                                            | Public    |
|                      | `/api/inventory/admin/`                          | GET    | Get all inventory with available quantity               | Admin     |
|                      | `/api/inventory/admin/{PID}`                     | GET    | Get single inventory item admin with available quantity | Admin     |
|                      | `/api/inventory/admin`                           | POST   | Create Inventory                                        | Admin     |
|                      | `/api/inventory/admin/{PID}`                     | PUT    | Edit Inventory                                          | Admin     |
|                      | `/api/inventory/admin/{PID}`                     | DELETE | Delete Inventory                                        | Admin     |
|                      | `/api/inventory/system/place-order/`             | PUT    | Place an order                                          | System    |
|                      | `/api/inventory/system/cancel-order/`            | PUT    | Cancel an order                                         | System    |
| User Management      | `/api/user/basic`                                | GET    | Get user details                                        | Basic     |
|                      | `/api/user/basic`                                | DELETE | Delete User                                             | Basic     |
|                      | `/api/user/basic`                                | PUT    | Edit User                                               | Basic     |
|                      | `/api/user/admin/`                               | GET    | View all users                                          | Admin     |
|                      | `/api/user/admin/{email}`                        | GET    | View Specific User                                      | Admin     |
|                      | `/api/user/admin/{email}`                        | DELETE | Delete specific User                                    | Admin     |
|                      | `/api/user/system`                               | POST   | Create User                                             | System    |
|                      | `/api/user/system/{email}`                       | GET    | Get User                                                | System    |
| Order Management     | `/api/order/basic`                               | POST   | Create Order                                            | Basic     |
|                      | `/api/order/basic`                               | GET    | Get all orders for user                                 | Basic     |
|                      | `/api/order/basic/{orderId}`                     | GET    | Get specific order                                      | Basic     |
|                      | `/api/order/basic/{orderId}`                     | DELETE | Cancel Order                                            | Basic     |
|                      | `/api/order/deliverer/status/{orderId}/{status}` | PUT    | Update order status to dispatched, and delivered        | Deliverer |
|                      | `/api/order/deliverer`                           | GET    | Get all orders                                          | Deliverer |
|                      | `/api/order/deliverer/{orderId}`                 | GET    | Get any order                                           | Deliverer |
|                      | `/api/order/admin/process/{orderId}`             | PUT    | Update Order Status                                     | Admin     |
|                      | `/api/order/admin`                               | GET    | Get all orders                                          | Admin     |
|                      | `/api/order/admin/{orderId}`                     | GET    | Get any order                                           | Admin     |