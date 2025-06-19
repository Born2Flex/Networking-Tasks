# Networking Practice Tasks

## Task 1. JSON mapping

Create your own mapper (accepts Object, returns a string, must pass validation on a JSON string).
Do it recursively with nested objects. (User, some fields ints, strings, Address)

## Task 2. HTTP/HTTPS on Sockets

Given JSON as a string, you need to send a correct HTTP and HTTPS POST request using sockets to some endpoint
(a simple application with one endpoint), the application should read this object from JSON and give a response, 
the socket should receive a 200 response.

## Task 3. NASA tasks
   Register in https://api.nasa.gov/, get API_KEY
### 3.1. Find the largest picture endpoint:
   - Create an app
   - Perform a GET request to this endpoint: https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=300&api_key=YOUR_API_KEY
   - Choose any HTTP client you want
   - Make sure you specify your API_KEY
   - Process  the response
   - Parse the JSON
   - Find the largest picture
   - NASA Open APIs 
### 3.2. Build the largest picture endpoint:
   - Create a new web app
   - Build an HTTP GET endpoint that shows the largest Mars picture
   - Accept a sol parameter
   - Get picture URLs from this endpoint: https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos (make sure you specify your api_key and pass sol)
   - Find the largest picture (make sure you handle redirects properly)
   - Return the actual picture file so the browser displays the image when opening your endpoint
   - Optimise your solution
   - Make a couple comments about your thoughts on optimization