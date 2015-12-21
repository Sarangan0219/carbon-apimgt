/*
 *  Copyright WSO2 Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.apimgt.rest.api.util.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;
import org.wso2.carbon.apimgt.rest.api.util.dto.ErrorDTO;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.EOFException;

public class GlobalThrowableMapper implements ExceptionMapper<Throwable> {

    private static final Log log = LogFactory.getLog(GlobalThrowableMapper.class);

    private ErrorDTO e500 = new ErrorDTO();

    GlobalThrowableMapper() {
        e500.setCode((long) 500);
        e500.setMessage("Internal server error");
        e500.setMoreInfo("");
        e500.setDescription("The server encountered an internal error. Please contact administrator.");
    }

    @Override
    public Response toResponse(Throwable e) {

        if (e instanceof ClientErrorException) {
            log.error("Client error", e);
            return ((ClientErrorException) e).getResponse();
        }

        if (e instanceof NotFoundException) {
            log.error("Resource not found", e);
            return ((NotFoundException) e).getResponse();
        }

        if (e instanceof PreconditionFailedException) {
            log.error("Precondition failed", e);
            return ((PreconditionFailedException) e).getResponse();
        }

        if (e instanceof BadRequestException) {
            log.error("Bad request", e);
            return ((BadRequestException) e).getResponse();
        }

        if (e instanceof ConstraintViolationException) {
            log.error("Constraint violation", e);
            return ((ConstraintViolationException) e).getResponse();
        }

        if (e instanceof ForbiddenException) {
            log.error("Resource forbidden", e);
            return ((ForbiddenException) e).getResponse();
        }

        if (e instanceof ConflictException) {
            log.error("Conflict", e);
            return ((ConflictException) e).getResponse();
        }

        if (e instanceof MethodNotAllowedException) {
            log.error("Method not allowed", e);
            return ((MethodNotAllowedException) e).getResponse();
        }

        if (e instanceof JsonParseException) {
            String errorMessage = "Malformed request body.";
            log.error(errorMessage, e);
            //noinspection ThrowableResultOfMethodCallIgnored
            return RestApiUtil.buildBadRequestException(errorMessage).getResponse();
        }

        if (e instanceof JsonMappingException) {
            if (e instanceof UnrecognizedPropertyException) {
                UnrecognizedPropertyException unrecognizedPropertyException = (UnrecognizedPropertyException) e;
                String unrecognizedProperty = unrecognizedPropertyException.getUnrecognizedPropertyName();
                String errorMessage = "Unrecognized property '" + unrecognizedProperty + "'";
                log.error(errorMessage, e);
                //noinspection ThrowableResultOfMethodCallIgnored
                return RestApiUtil.buildBadRequestException(errorMessage).getResponse();
            } else {
                String errorMessage = "One or more request body parameters contain disallowed values.";
                log.error(errorMessage, e);
                //noinspection ThrowableResultOfMethodCallIgnored
                return RestApiUtil.buildBadRequestException(errorMessage).getResponse();
            }
        }

        //This occurs when received an empty body in an occasion where the body is mandatory
        if (e instanceof EOFException) {
            String errorMessage = "Request payload cannot be empty.";
            log.error(errorMessage, e);
            //noinspection ThrowableResultOfMethodCallIgnored
            return RestApiUtil.buildBadRequestException(errorMessage).getResponse();
        }

        //unknown exception log and return
        log.error("An Unknown exception has been captured by global exception mapper.", e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
                .entity(e500).build();
    }
}
