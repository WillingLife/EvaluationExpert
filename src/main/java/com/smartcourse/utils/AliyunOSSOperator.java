package com.smartcourse.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.ObjectMetadata;
import com.smartcourse.properties.AliyunOSSProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class AliyunOSSOperator {
    // 阿里云配置信息
    private final AliyunOSSProperties aliyunOSSProperties;

    public String upload(byte[] data, String originalFileName){
        String endpoint = aliyunOSSProperties.getEndpoint();
        String bucketName = aliyunOSSProperties.getBucketName();
        String region = aliyunOSSProperties.getRegion();

        String objectName = generateObjectName(originalFileName);

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
        try{
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(data), metadata);
        }finally {
            ossClient.shutdown();
        }

        // 返回url
        return  bucketName+objectName;
    }

    /**
     * 生成对象名称（文件路径）
     */
    private String generateObjectName(String originalFileName) {
        // 获取当前日期目录
        LocalDate currentDate = LocalDate.now();
        String dir = currentDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 生成唯一文件名
        UUID uuid = UUID.randomUUID();
        String fileExtension = getFileExtension(originalFileName);
        String newFileName = uuid + fileExtension;

        return dir + "/" + newFileName;
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
            default -> "application/octet-stream";
        };
    }
}