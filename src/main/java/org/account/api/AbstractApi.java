package org.account.api;

import org.account.model.dto.ErrorDTO;

import javax.ws.rs.core.Response;

public abstract class AbstractApi {

    protected Response ok() {
        return Response.status(Response.Status.OK).build();
    }

    protected Response ok(Object entity) {
        return Response.status(Response.Status.OK).entity(entity).build();
    }

    protected Response badRequest(String errorMsg) {
        return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorDTO(errorMsg)).build();
    }

    protected Response notAcceptable(String errorMsg) {
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorDTO(errorMsg)).build();
    }

    protected Response notFound(String errorMsg) {
        return Response.status(Response.Status.NOT_FOUND).entity(new ErrorDTO(errorMsg)).build();
    }
}
