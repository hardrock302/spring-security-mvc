This is the Readme for the user service using Spring Boot 2.7.3. It uses mysql for a database. Running it requires eclipse and the spring tools.

Import it into eclipse as an existing maven project and choose Run As->Spring Boot App
It currently runs on port 8080, but it can be changed through the application.properties file



**Any operation save register now requires a json web token**

The json payloads sent in postman body of the request must adhere to the following

{
    "id": int,
    "first_name": string,
    "last_name": string,
    "email": string,
    "enabled": int,
    "password": string,
    "phone_number": string,
    "membership_level": string,
    "membership_expiry_date": string [yyyy-mm-dd|null]
}

Values for the enabled column are restricted to 0,1,2 with zero being inactive and 1 being active, 2 is reserved for banned accounts.
Phone numbers must be in the form country code-area-code-xxx-xxxx/
Membership Levels maybe UNPAID, GOLD, or DIAMOND

Passwords should be encrypted asymmetically between server and client. For now, they are required to be Base64 UTF-8 encoded when sent in a payload.

You may get a user by email using GET, update a user by id using PUT and sending the appropriate fields, create a user using POST and delete a user using DELETE

All fields save enabled and id are required on create. The sql scripts are in the eclipse project under src/main/resources and will work in MYSQL community edition.

Employee endpoints can be accessed having the role ROLE_ADMIN at /users/admin respectively.

Performing an update:

When updating a user, one must provide a field in the json named "oldEmail" with the current email of the user in the database regardless of whether the email is modified.



Logging in:

Loogging in can be done with a {"email": "email value", "password": "Base-64 encoded password"}

A successful login will return a 200 status code and an authorization token in the authorization field in the header.

The token is currently set to be invalidated after 2 hours. The token should be provided under an Authorization header in a request either programatically or using the headers or Authorization/Bearer Token option in Postman

The token is required to perform operations other than registering in the api.
