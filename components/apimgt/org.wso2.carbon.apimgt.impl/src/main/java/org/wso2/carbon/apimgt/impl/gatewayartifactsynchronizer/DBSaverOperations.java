package org.wso2.carbon.apimgt.impl.gatewayartifactsynchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dao.constants.SQLConstants;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBSaverOperations implements ArtifactSaverOperations {

    private static final Log log = LogFactory.getLog(DBSaverOperations.class);
    private static DBSaverOperations INSTANCE = null;


    /**
     * Method to get the instance of the ApiMgtDAO.
     *
     * @return {@link DBSaverOperations} instance
     */
    public static DBSaverOperations getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DBSaverOperations();
        }

        return INSTANCE;
    }

    @Override
    public boolean isAPIPublishedInAnyGateway(String APIId) throws APIManagementException {

        int count = 0;
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLConstants.GET_PUBLISHED_GATEWAYS_FOR_API)) {
            statement.setString(1, APIId);
            statement.setString(2, APIConstants.GatewayArtifactSynchronizer.GATEWAY_INSTRUCTION_PUBLISH);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                count = rs.getInt("COUNT");
            }
        } catch (SQLException e) {
            handleException("Failed check whether API is published in any gateway " + APIId, e);
        }
        return count != 0;
    }


    @Override
    public boolean isAPIDetailsExists(String APIId) throws APIManagementException {

        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement(SQLConstants.GET_GATEWAY_PUBLISHED_API_DETAILS)) {
            statement.setString(1, APIId);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            handleException("Failed to check API details status of API with ID " + APIId, e);
        }
        return false;
    }

    @Override
    public void addGatewayPublishedAPIDetails(String APIId, String APIName, String version, String tenantDomain)
            throws APIManagementException {

        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLConstants.ADD_GW_PUBLISHED_API_DETAILS)) {
            statement.setString(1, APIId);
            statement.setString(2, APIName);
            statement.setString(3, version);
            statement.setString(4, tenantDomain);
            statement.executeUpdate();
        } catch (SQLException e) {
            handleException("Failed to add API details for " + APIName, e);
        }
    }

    @Override
    public boolean isAPIArtifactExists(String APIId, String gatewayLabel) throws APIManagementException {

        int count = 0;
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLConstants.CHECK_ARTIFACT_EXISTS)) {
            statement.setString(1, APIId);
            statement.setString(2, gatewayLabel);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                count = rs.getInt("COUNT");
            }
        } catch (SQLException e) {
            handleException("Failed to check API artifact status of API with ID " + APIId + " for label "
                    + gatewayLabel, e);
        }
        return count != 0;
    }

    @Override
    public void addGatewayPublishedAPIArtifacts(String APIId, String gatewayLabel, ByteArrayInputStream bais,
                                                int streamLength, String gatewayInstruction, String query)
            throws APIManagementException {

        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBinaryStream(1, bais, streamLength);
            statement.setString(2, gatewayInstruction);
            statement.setString(3, APIId);
            statement.setString(4, gatewayLabel);
            statement.executeUpdate();
        } catch (SQLException e) {
            handleException("Failed to add artifacts for " + APIId, e);
        }
    }


    private void handleException(String msg, Throwable t) throws APIManagementException {
        log.error(msg, t);
        throw new APIManagementException(msg, t);
    }
}
