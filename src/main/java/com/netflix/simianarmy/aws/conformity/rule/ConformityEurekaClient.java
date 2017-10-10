package com.netflix.simianarmy.aws.conformity.rule;

/**
 * The interface for a client to access Eureka service to get the status of instances for Conformity Monkey.
 */
public interface ConformityEurekaClient {
    /**
     * Checks whether an instance has health check url in Eureka.
     * @param region the region of the instance
     * @param instanceId the instance id
     * @return true if the instance has health check url in Eureka, false otherwise.
     */
    boolean hasHealthCheckUrl(String region, String instanceId);

    /**
     * Checks whether an instance has status url in Eureka.
     * @param region the region of the instance
     * @param instanceId the instance id
     * @return true if the instance has status url in Eureka, false otherwise.
     */
    boolean hasStatusUrl(String region, String instanceId);

    /**
     * Checks whether an instance is healthy in Eureka.
     * @param region the region of the instance
     * @param instanceId the instance id
     * @return true if the instance is healthy in Eureka, false otherwise.
     */
    boolean isHealthy(String region, String instanceId);
}
