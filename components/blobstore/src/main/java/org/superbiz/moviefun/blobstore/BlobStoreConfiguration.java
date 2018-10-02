package org.superbiz.moviefun.blobstore;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.superbiz.moviefun.ServiceCredentials;

@Configuration
public class BlobStoreConfiguration {

    @Bean
    ServiceCredentials serviceCredentials(@Value("${vcap.services}") String vcapServices) {
        return new ServiceCredentials(vcapServices);
    }

    @Bean
    public BlobStore blobStore(
            ServiceCredentials serviceCredentials,
            @Value("${s3.endpointUrl:#{null}}") String s3EndpointUrl
    ) {
        String s3AccessKey = serviceCredentials.getCredential("moviefun-s3", "access_key_id");
        String s3SecretKey = serviceCredentials.getCredential("moviefun-s3", "secret_access_key");
        String s3BucketName = serviceCredentials.getCredential("moviefun-s3", "bucket");

        AWSCredentials credentials = new BasicAWSCredentials(s3AccessKey, s3SecretKey);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        if (s3EndpointUrl != null) {
            s3Client.setEndpoint(s3EndpointUrl);
        }

        return new S3Store(s3Client, s3BucketName);
    }

}