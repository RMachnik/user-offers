# USER OFFERS REST API

Simple service with user and offers resources available via REST API.

Decided to use javalin to have some fun with new
small java server based on jetty.

In general, in that code I would add more logging if this would go to prod. As for now
there are only in memory repositories implemented.

There is nice openapi integration available for javalin so api could
be easily documented and exposed as swagger endpoint.

## API

Api available under `/user-offers/api/users` and `/user-offers/api/offers`.
