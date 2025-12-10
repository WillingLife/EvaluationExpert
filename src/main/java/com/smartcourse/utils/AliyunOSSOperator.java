package com.smartcourse.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.ObjectMetadata;
import com.smartcourse.properties.AliyunOSSProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class AliyunOSSOperator {
    // 阿里云配置信息
    private final AliyunOSSProperties aliyunOSSProperties;

    public String upload(byte[] data, String originalFileName,String prefix) {
        String endpoint = aliyunOSSProperties.getEndpoint();
        String bucketName = aliyunOSSProperties.getBucketName();
        String region = aliyunOSSProperties.getRegion();

        String objectName = generateObjectName(originalFileName);
        if(prefix!=null&&!prefix.isEmpty()){
            objectName = prefix+"/"+objectName;
        }

        // 创建OSSClient实例。
        // 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        DefaultCredentialProvider credentialProvider = new DefaultCredentialProvider(
                aliyunOSSProperties.getAccessKeyId(),
                aliyunOSSProperties.getAccessKeySecret()
        );

        // 创建元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        metadata.setContentType(getContentType(originalFileName));

        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        // 简单上传文件
        try {
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(data), metadata);
        } finally {
            ossClient.shutdown();
        }

        // 返回url
        return objectName;
    }


    /**
     * 生成对象名称（文件路径）
     */
    private String generateObjectName(String originalFileName) {
        // 获取当前日期目录

        // 生成唯一文件名
        UUID uuid = UUID.randomUUID();
        String fileExtension = getFileExtension(originalFileName);
        return uuid + fileExtension;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex > 0) ? fileName.substring(lastDotIndex) : "";
    }

    /**
     * 获取文件Content-Type
     */
    private String getContentType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return switch (extension) {
            case ".pdf" -> "application/pdf";
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".doc" -> "application/msword";
            case ".docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".txt" -> "text/plain";
            default -> "application/octet-stream";
        };
    }

    public String getUrl(String fileName) {
        String endpoint = aliyunOSSProperties.getEndpoint();
        String bucketName = aliyunOSSProperties.getBucketName();
        String region = aliyunOSSProperties.getRegion();
        String accessKeyId = aliyunOSSProperties.getAccessKeyId();
        String accessKeySecret = aliyunOSSProperties.getAccessKeySecret();
        DefaultCredentialProvider provider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(provider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            // 设置预签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
            Date expiration = new Date(new Date().getTime() + 3600 * 1000L);
            // 生成以GET方法访问的预签名URL。本示例没有额外请求头，其他人可以直接通过浏览器访问相关内容。
            URL url = ossClient.generatePresignedUrl(bucketName, fileName, expiration);
            return url.toString();
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return null;
    }


}