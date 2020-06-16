package org.wso2.carbon.apimgt.impl.gatewayartifactsynchronizer;

import org.wso2.carbon.apimgt.api.APIManagementException;

import java.io.ByteArrayInputStream;

public interface ArtifactSaverOperations {


    /**
     * Check whether the API is published in any of the Gateways
     *
     * @param APIId - UUID of the API
     * @throws APIManagementException if an error occurs
     */
    boolean isAPIPublishedInAnyGateway(String APIId) throws APIManagementException;

    /**
     * Check whether the API details exists in the db
     *
     * @param APIId - UUID of the API
     * @throws APIManagementException if an error occurs
     */
    boolean isAPIDetailsExists(String APIId) throws APIManagementException;


    /**
     * Add details of the APIs published in the Gateway
     *
     * @param APIId        - UUID of the API
     * @param APIName      - Name of the API
     * @param version      - Version of the API
     * @param tenantDomain - Tenant domain of the API
     * @throws APIManagementException if an error occurs
     */
    void addGatewayPublishedAPIDetails(String APIId, String APIName, String version, String tenantDomain)
            throws APIManagementException;


    /**
     * Check whether the API artifact for given label exists in the db
     *
     * @param APIId - UUID of the API
     * @throws APIManagementException if an error occurs
     */
    boolean isAPIArtifactExists(String APIId, String gatewayLabel) throws APIManagementException;



    /**
     * Add or update details of the APIs published in the Gateway
     *
     * @param APIId        - UUID of the API
     * @param gatewayLabel - Published gateway's label
     * @param bais         - Byte array Input stream of the serializide gatewayAPIDTO
     * @param streamLength - Length of the stream
     * @throws APIManagementException if an error occurs
     */
    void addGatewayPublishedAPIArtifacts(String APIId, String gatewayLabel, ByteArrayInputStream bais,
                                         int streamLength, String gatewayInstruction, String query)
            throws APIManagementException;
}
