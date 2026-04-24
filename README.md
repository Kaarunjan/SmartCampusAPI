# Smart Campus API

## API Overview
Smart Campus API is build to montior and manage each sensor for each rooms inside the University Campus area. Its provide a seamless interface for campus facility manager and automated the building systems with campus infrastructure data.

### Core Functionalities
- Create and manage campus rooms with capacity tracking
- Register sensors and assign them to specific rooms
- Filter sensors by type (e.g. Temperature, CO2, Occupancy)
- Record and retrieve historical sensor readings
- Automatic update of a sensor's current value upon new readings
- Full error handling with meaningful HTTP status codes and JSON responses

### Technology Stack
- Java with JAX-RS (Jersey 2.32)
- In-memory storage using ConcurrentHashMap
- Deployed on Apache Tomcat via NetBeans

## How to Build and Run
(step by step instructions)

### Prerequisites
- Java JDK 8 or Higher
- Any Java supported IDE (Netbeans, Intelije, Eclipse)
- Apache Tomcat Server
- Maven (bundled with netbeans)
- Postman (for testing)


### steps
1. Clone the repository
   git clone https://github.com/Kaarunjan/SmartCampusAPI.git

2. Open the project in NetBeans or any other IDEs
   - File → Open Project
   - Navigate to the cloned folder and select it

3. Build the project
   - Right-click the project → Clean and Build
   - Ensure you see BUILD SUCCESS in the output tab

3.5. Optional: Run the server


4. Run the project
   - Right-click the project → Run
   - NetBeans will automatically deploy to Tomcat

5. The API will be available at
   http://localhost:8080/api/v1

## Sample curl Commands
(at least 5 examples)

## Sample curl Commands

### 1. Get API Discovery
curl -X GET http://localhost:8080/api/v1

### 2. Create a Room
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\": \"LIB-301\", \"name\": \"Library Quiet Study\", \"capacity\": 50}"

### 3. Get All Rooms
curl -X GET http://localhost:8080/api/v1/rooms

### 4. Create a Sensor
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\": \"TEMP-001\", \"type\": \"Temperature\", \"status\": \"ACTIVE\", \"currentValue\": 22.5, \"roomId\": \"LIB-301\"}"

### 5. Get Sensors by Type
curl -X GET http://localhost:8080/api/v1/sensors?type=Temperature

### 6. Add a Sensor Reading
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\": 24.5}"

### 7. Delete a Room
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301

### 8. Get the summary
curl -X GET ## Sample curl Commands

### 1. Get API Discovery
curl -X GET http://localhost:8080/api/v1

### 2. Create a Room
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\": \"LIB-301\", \"name\": \"Library Quiet Study\", \"capacity\": 50}"

### 3. Get All Rooms
curl -X GET http://localhost:8080/api/v1/rooms

### 4. Create a Sensor
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\": \"TEMP-001\", \"type\": \"Temperature\", \"status\": \"ACTIVE\", \"currentValue\": 22.5, \"roomId\": \"LIB-301\"}"

### 5. Get Sensors by Type
curl -X GET http://localhost:8080/api/v1/sensors?type=Temperature

### 6. Add a Sensor Reading
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\": 24.5}"

### 7. Delete a Room
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301

## Report - Question Answers

### Part 1 - Setup & Discovery
**Q1: JAX-RS Resource Lifecycle**
By default, JAX-RX initiate a new intance for each new resource classes and ever incoming request.
This is known as the per-request lifecycle. This means that any data stored as an instance variable
inside a resource class would be lost immediately after the request is completed, as the object is discarded.

This has a direct impact on how in-memory data must be managed. Since each 
request gets a fresh resource instance, shared data cannot be stored inside 
the resource class itself. Instead, a separate singleton DataStore class is 
used, which holds all data in ConcurrentHashMap collections. Being a singleton,
only one instance of DataStore exists throughout the entire application 
lifetime, meaning all resource instances access and modify the same shared data.

ConcurrentHashMap is used instead of a regular HashMap because multiple 
requests can arrive simultaneously. ConcurrentHashMap is thread-safe, 
preventing race conditions and data corruption when multiple requests 
read or write data at the same time.

**Q2: HATEOS**
HATEOAS is considered a hallmark of advanced RESTful design because it makes 
an API self-descriptive and navigable. Instead of a client needing to 
hardcode URLs or rely on external documentation, each API response includes 
links to related resources and available actions. similar to how web browser
navigate through it content with hyperlink, the APIs uses the linke navigate 
to next part.

For example, in our Discovery endpoint GET /api/v1, the response includes 
a _links object containing URLs to /api/v1/rooms and /api/v1/sensors. 
A client developer can immediately discover and navigate to these resources 
without needing to read separate documentation.

The key benefits over static documentation are:
- Static documentation can become outdated when the API changes, whereas 
  HATEOAS responses always reflect the current state of the API
- Client developers spend less time reading documentation and can explore 
  the API dynamically through the responses themselves
- It reduces tight coupling between the client and server, meaning the 
  server can change URLs without breaking clients that follow the links




### Part 2 - Room Management
**Q3: IDs vs Full Objects**
There are clear tradeoffs between returning only IDs versus full room objects 
when listing rooms.

Returning only IDs:
- Reduces network bandwidth significantly as the response payload is much smaller
- Requires the client to make additional requests for each room to get its 
  full details, which increases the number of HTTP calls and overall latency
- This approach is suitable when the client only needs to know what rooms 
  exist, not their details

Returning full room objects:
- Increases the response payload size as all room data is included
- Eliminates the need for additional requests since the client already has 
  all the information it needs in a single response
- Reduces client-side processing as no further API calls are needed
- This approach is more practical for our Smart Campus API since facilities 
  managers need to see room details like name, capacity and assigned sensors 
  immediately

In our implementation we return full room objects for GET /api/v1/rooms 
because it provides a better experience for the client and reduces the 
number of round trips to the server, which is more efficient in a 
campus management context.

**Q4: DELETE Idempotency**
There are clear tradeoffs between returning only IDs versus full room objects 
when listing rooms.

Returning only IDs:
- Reduces network bandwidth significantly as the response payload is much smaller
- Requires the client to make additional requests for each room to get its 
  full details, which increases the number of HTTP calls and overall latency
- This approach is suitable when the client only needs to know what rooms 
  exist, not their details

Returning full room objects:
- Increases the response payload size as all room data is included
- Eliminates the need for additional requests since the client already has 
  all the information it needs in a single response
- Reduces client-side processing as no further API calls are needed
- This approach is more practical for our Smart Campus API since facilities 
  managers need to see room details like name, capacity and assigned sensors 
  immediately

In our implementation we return full room objects for GET /api/v1/rooms 
because it provides a better experience for the client and reduces the 
number of round trips to the server, which is more efficient in a 
campus management context.


## Part 3 - Sensor Operations & Filtering

**Q5: @Consumes Annotation**

The @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method tells 
JAX-RS that this endpoint only accepts requests with a Content-Type header 
of application/json. If a client attempts to send data in a different format 
such as text/plain or application/xml, JAX-RS will automatically reject the 
request before it even reaches the resource method.

The technical consequences are as follows:

- JAX-RS will return an HTTP 415 Unsupported Media Type response immediately
- The resource method body never executes, meaning no data processing occurs
- The client receives a clear error response indicating the format mismatch
- This protects the API from receiving malformed or unexpected data formats

This is a key benefit of using @Consumes — it acts as a first line of 
defence by enforcing a strict contract between the client and server on 
what data format is acceptable. Without this annotation, the API would 
attempt to parse the incoming data as JSON regardless of its format, 
which would likely cause a parsing error and an unhandled exception on 
the server side.

**Q6: @QueryParam vs Path Parameter for Filtering**

Using a query parameter such as GET /api/v1/sensors?type=CO2 is considered 
superior to embedding the filter in the URL path such as 
/api/v1/sensors/type/CO2 for several reasons.

In REST architecture, URL paths should represent resources and their 
hierarchy, not filtering criteria. /api/v1/sensors represents the sensors 
collection as a resource, while ?type=CO2 is simply a filter applied to 
that collection. Embedding the filter in the path would imply that 
"type/CO2" is a separate resource, which is semantically incorrect.

Additional benefits of query parameters:
- They are optional by nature, meaning GET /api/v1/sensors still works 
  without any filter and returns all sensors
- Multiple filters can be combined easily such as ?type=CO2&status=ACTIVE
- They are the universally recognised convention for searching and filtering 
  collections in REST APIs
- Path parameters are better suited for identifying a specific resource 
  such as /api/v1/sensors/{sensorId}

Using path parameters for filtering would also make the API harder to 
extend in the future, as adding new filter options would require defining 
new URL paths rather than simply adding new query parameters.

### Part 4 - Sub-Resources

**Q7: Sub-Resource Locator Pattern Benefits**

The Sub-Resource Locator pattern involves delegating the handling of a 
nested resource path to a separate dedicated class. In our implementation, 
when a request comes in for /api/v1/sensors/{sensorId}/readings, the 
SensorResource class does not handle it directly. Instead it returns an 
instance of SensorReadingResource which handles all reading related operations.

The architectural benefits of this pattern are:

- Separation of concerns: Each resource class has a single responsibility. 
  SensorResource manages sensors while SensorReadingResource manages readings. 
  This makes the code easier to understand and maintain.

- Reduced complexity: Without this pattern, all nested paths would need to 
  be defined in one massive resource class. As the API grows this would 
  become increasingly difficult to manage and debug.

- Reusability: The SensorReadingResource class can be reused or extended 
  independently without affecting the parent SensorResource class.

- Scalability: In large APIs with many nested resources, this pattern allows 
  teams to work on different resource classes simultaneously without 
  conflicts, making the codebase more scalable and maintainable.


  ### Part 5 - Error Handling & Logging

**Q8: HTTP 422 vs 404 for Missing Reference**

When a client sends a POST request to create a new sensor with a roomId 
that does not exist, HTTP 422 Unprocessable Entity is more semantically 
accurate than HTTP 404 Not Found for the following reasons.

HTTP 404 Not Found means the requested URL or endpoint could not be found 
on the server. In this case the endpoint /api/v1/sensors exists and is 
perfectly valid, so returning 404 would be misleading to the client.

HTTP 422 Unprocessable Entity means the server understood the request, 
the endpoint exists, the JSON format is correct, but the content of the 
request is semantically invalid. In our case the JSON payload is valid 
but the roomId value inside it references a resource that does not exist, 
making the request impossible to process correctly.

This distinction is important because:
- It gives the client more precise information about what went wrong
- The client knows the issue is with the data content, not the URL
- It helps developers debug issues faster as the error is more descriptive
- It follows REST best practices of using the most semantically accurate 
  HTTP status code for each situation

**Q9: Security Risks of Exposing Stack Traces**

Exposing internal Java stack traces to external API consumers poses 
significant cybersecurity risks. Our Global Exception Mapper prevents 
this by catching all unexpected errors and returning a generic HTTP 500 
response without any internal details.

The specific information an attacker could gather from a stack trace includes:

- Class and package names: Reveals the internal structure and architecture 
  of the application, helping attackers understand how the system is built
- Method names and line numbers: Pinpoints exactly where the error occurred, 
  making it easier to identify and exploit vulnerabilities
- Library and framework versions: Exposes which third party libraries are 
  being used and their versions, allowing attackers to look up known 
  vulnerabilities for those specific versions
- File paths: Reveals the server's directory structure and file system 
  organisation
- Database or data structure details: Can reveal how data is stored and 
  managed internally

By returning a generic error message such as "An unexpected error occurred. 
Please contact the administrator", we prevent all of this information from 
reaching potential attackers while still informing the client that something 
went wrong.

**Q10: JAX-RS Filters vs Manual Logging**

Using JAX-RS filters for cross-cutting concerns like logging is far superior 
to manually inserting Logger.info() statements inside every resource method 
for several reasons.

- Single point of implementation: The logging filter is implemented once in 
  one class and automatically applies to every single request and response 
  in the API. Manual logging would require adding Logger statements to every 
  resource method individually.

- Consistency: Filters guarantee that every request and response is logged 
  in exactly the same format. Manual logging risks inconsistency as different 
  developers may log different information in different formats.

- Maintainability: If the logging format needs to change, only the filter 
  class needs to be updated. With manual logging, every resource method 
  would need to be updated individually, increasing the risk of errors.

- Separation of concerns: Resource methods should focus solely on business 
  logic. Mixing logging code into resource methods violates the single 
  responsibility principle and makes the code harder to read and maintain.

- No risk of forgetting: With manual logging it is easy to forget to add 
  Logger statements to new resource methods. Filters automatically cover 
  all current and future endpoints without any additional effort.


  ## API Design

The API follows a RESTful resource hierarchy based on the physical 
structure of the campus:

- /api/v1                          — Discovery endpoint
- /api/v1/rooms                    — Room management
- /api/v1/rooms/{roomId}           — Specific room operations
- /api/v1/sensors                  — Sensor management
- /api/v1/sensors?type={type}      — Filter sensors by type
- /api/v1/sensors/{sensorId}       — Specific sensor operations
- /api/v1/sensors/{sensorId}/readings — Sensor reading history