# mod-inventory-storage

Copyright (C) 2017 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.

# Goal

FOLIO compatible inventory storage module.

Provides PostgreSQL based storage to complement the [inventory module](http://github.com/folio-org/mod-inventory). Written in Java, using the raml-module-builder and uses Maven as its build system.

# Prerequisites

- Java 8 JDK
- Maven 3.3.9
- Postgres 9.6.1 (running and listening on localhost:5432, logged in user must have admin rights)
- Node 6.4.0 (for API linting and documentation generation)
- NPM 3.10.3 (for API linting and documentation generation)
- Python 3.6.0 (for nice to have parts of deployment scripts, e.g. is used to remove environment variables from Okapi)

# Preparation

## Git Submodules

There are some common RAML definitions that are shared between FOLIO projects via Git submodules.

To initialise these please run `git submodule init && git submodule update` in the root directory.

If these are not initialised, the module will fail to build correctly, and other operations may also fail.

More information is available on the [developer site](http://dev.folio.org/doc/setup#update-git-submodules).

## Postgres

Run the `setup-test-db.sh` script in the root directory to setup Postgres with a database to be used in tests.
This is only required to run tests against an external Postgres instance, the default is to use an embedded Postgres instance.

# Building

run `mvn install` from the root directory.

To run the tests against both embedded and external databases, run `./build.sh` from the root directory.

# Running

## Preparation

## Sample Data

Importing/ingesting of sample data is done via Okapi, so modules must be registered prior to importing.

To load some sample data run `./import-sample-data.sh` from the root directory.

## Running Okapi

Make sure that [Okapi](https://github.com/folio-org/okapi) is running on its default port of 9130 (see the [guide](https://github.com/folio-org/okapi/blob/master/doc/guide.md) for instructions).

A script for building and running Okapi is provided. Run `../mod-inventory-storage/start-okapi.sh` from the root of the Okapi source.

As this runs Okapi using Postgres storage, some database preparation is required. This can be achieved by running `./create-okapi-database.sh` from the root of this repository.

## Managed by Okapi

To register the module with deployment instructions and activate it for a demo tenant, run `./start-managed-demo.sh` from the root directory.

To deactivate and unregister the module, run `./stop-managed-demo.sh` from the root directory.

## Natively

Run `./start.sh` from the root directory.

To stop them, run `./stop.sh` from the root directory.

# Making Requests

These modules provide HTTP based APIs rather than any UI themselves.

As FOLIO is a multi-tenant system, many of the requests made to these modules are tenant aware (via the X-Okapi-Tenant header), which means most requests need to be made via a system which understands these headers (e.g. another module or UI built using [Stripes](https://github.com/folio-org/stripes-core)).

Therefore, it is suggested that requests to the API are made via tools such as curl or [postman](https://www.getpostman.com/), or via a browser plugin for adding headers, such as [Requestly](https://chrome.google.com/webstore/detail/requestly/mdnleldcmiljblolnjhpnblkcekpdkpa).

## Okapi Root Address

It is recommended that the modules are located via Okapi. Access via Okapi requires passing the X-Okapi-Tenant header (see the Okapi guide above for details).

http://localhost:9130/instance-storage
http://localhost:9130/item-storage

# Operating System Support

Most of the development for these modules, thus far, has been performed on OS X, with some on Ubuntu. Feedback for these, and particularly other operating systems is very welcome.

# Additional Information

The guide and other [documentation](doc) for this module.

Other [modules](http://dev.folio.org/source-code/#server-side).

See project [MODINVSTOR](https://issues.folio.org/browse/MODINVSTOR)
at the [FOLIO issue tracker](http://dev.folio.org/community/guide-issues).

Other FOLIO Developer documentation is at [dev.folio.org](http://dev.folio.org/)

# Appendix 1 - Okapi Registration

As an alternative to the demo scripts, it is possible to perform Okapi tenant and module setup via a set of smaller scripts, see below for more information on these steps.

### Preparing a Tenant

Run `./create-tenant.sh` from the root directory to set up a test tenant (named our).

The output should be something similar to:

```
HTTP/1.1 201 Created
Content-Type: application/json
Location: /_/proxy/tenants/demo_tenant
Content-Length: 81

{
  "id" : "demo_tenant",
  "name" : "A Library",
  "description" : "A library"
}
```

The tenant can be deleted by running `./delete-tenant.sh`

### Registering the Modules With Okapi

Run `./register.sh` from the root directory.

The last output should be something similar to:

```
HTTP/1.1 201 Created
Content-Type: application/json
Location: /_/discovery/modules/knowledge-base-core/localhost-9401
Content-Length: 104

{
  "instId" : "localhost-9401",
  "srvcId" : "knowledge-base-core",
  "url" : "http://localhost:9401"
}
HTTP/1.1 201 Created
Content-Type: application/json
Location: /_/proxy/modules/knowledge-base-core
Content-Length: 375

{
  "id" : "knowledge-base-core",
  "name" : "Core Knowledge Base Module",
  "provides" : [ {
    "id" : "knowledge-base-core",
    "version" : "0.1"
  } ],
  "routingEntries" : [ {
    "methods" : [ "GET", "POST" ],
    "path" : "/knowledge-base",
    "level" : "30",
    "type" : "request-response",
    "permissionsRequired" : null,
    "permissionsDesired" : null
  } ]
}
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 34

{
  "id" : "knowledge-base-core"
}
```

# Appendix 2 - Docker Information

## When Using the Modules as Docker Containers

For the modules to communicate via Okapi Proxy, when running in Docker containers, the address for Okapi Proxy needs to be routable from inside the container.

This can be achieved by passing a parameter to the script used to start Okapi, as follows `../mod-metadata/start-okapi.sh http://192.168.X.X:9130`

Where 192.168.X.X is a routable IP address for the host from container instances and both repository clones are at the same directory level on your machine.

### Finding a Routable Address

Finding the appropriate IP address can be OS and Docker implementation dependent, so this is a very early guide rather than thorough treatment of the topic.

If these methods don't work for you, please do get in touch, so this section can be improved.

On Linux, `ifconfig docker0 | grep 'inet addr:'` should give output similar to `inet addr:192.168.X.X  Bcast:0.0.0.0  Mask:255.255.0.0`, , the first IP address is usually routable from within containers.

On Mac OS X (using Docker Native), `ifconfig en0 | grep 'inet '` should give output similar to `inet 192.168.X.X netmask 0xffffff00 broadcast 192.168.X.X`, the first IP address is usually routable from within containers.
