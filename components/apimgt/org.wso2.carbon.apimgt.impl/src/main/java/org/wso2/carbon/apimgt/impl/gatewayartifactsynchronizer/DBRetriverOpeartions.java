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
import java.util.ArrayList;
import java.util.List;

public class DBRetriverOpeartions implements ArtifactRetrieverOperations {

    private static final Log log = LogFactory.getLog(DBRetriverOpeartions .class);
    private static DBRetriverOpeartions  INSTANCE = null;


    /**
     * Method to get the instance of the ApiMgtDAO.
     *
     * @return {@link DBRetriverOpeartions } instance
     */
    public static DBRetriverOpeartions  getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DBRetriverOpeartions ();
        }

        return INSTANCE;
    }

    @Override
    public ByteArrayInputStream getGatewayPublishedAPIArtifacts(String APIId, String gatewayLabel,
                                                                String gatewayInstruction)
            throws APIManagementException {

        ByteArrayInputStream baip = null;
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLConstants.GET_API_ARTIFACT)) {
            statement.setString(1, APIId);
            statement.setString(2, gatewayLabel);
            statement.setString(3, gatewayInstruction);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                byte[] st = (byte[]) rs.getObject(1);
                baip = new ByteArrayInputStream(st);
            }
        } catch (SQLException e) {
            handleException("Failed to get artifacts of API with ID " + APIId, e);
        }
        return baip;
    }

    @Override
    public List<ByteArrayInputStream> getAllGatewayPublishedAPIArtifacts(String label)
            throws APIManagementException {

        List<ByteArrayInputStream> baip = new ArrayList<>();
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLConstants.GET_ALL_API_ARTIFACT)) {
            statement.setString(1, label);
            statement.setString(2, APIConstants.GatewayArtifactSynchronizer.GATEWAY_INSTRUCTION_PUBLISH);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                byte[] st = (byte[]) rs.getObject(1);
                ByteArrayInputStream byteArrayInputStream= new ByteArrayInputStream(st);
                baip.add(byteArrayInputStream);
            }
            return baip;
        } catch (SQLException e) {
            handleException("Failed to get artifacts " , e);
        }
        return baip;
    }

    private void handleException(String msg, Throwable t) throws APIManagementException {
        log.error(msg, t);
        throw new APIManagementException(msg, t);
    }

}
