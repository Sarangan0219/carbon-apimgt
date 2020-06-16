package org.wso2.carbon.apimgt.impl.gatewayartifactsynchronizer;

import org.wso2.carbon.apimgt.api.APIManagementException;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface ArtifactRetrieverOperations {


    /**
     * Retrieve the blob of the API
     *
     * @param APIId        - UUID of the API
     * @param gatewayLabel - Gateway label of the API
     * @throws APIManagementException if an error occurs
     */
    ByteArrayInputStream getGatewayPublishedAPIArtifacts(String APIId, String gatewayLabel,
                                                         String gatewayInstruction)
            throws APIManagementException;

    /**
     * Retrieve the list of blobs of the APIs for a given label
     *
     * @param label - Gateway label of the API
     * @throws APIManagementException if an error occurs
     */
    List<ByteArrayInputStream> getAllGatewayPublishedAPIArtifacts(String label)
            throws APIManagementException;

}
