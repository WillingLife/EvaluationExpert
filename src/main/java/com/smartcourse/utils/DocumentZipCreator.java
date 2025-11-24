package com.smartcourse.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DocumentZipCreator {

    /**
     * 从文档 URL 列表创建 ZIP 文件字节数组
     */
    public byte[] createZipFromDocumentUrls(List<String> fileList) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        try {
            for (int i = 0; i < fileList.size(); i++) {
                String documentUrl = fileList.get(i);
                // 下载文档并添加到 ZIP
                downloadAndAddToZip(zos, documentUrl, i);
            }
        } finally {
            zos.close();
        }

        return baos.toByteArray();
    }

    /**
     * 下载文档并添加到 ZIP 流
     */
    private void downloadAndAddToZip(ZipOutputStream zos, String documentUrl, int index) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(documentUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 获取文件名
                String fileName = getFileNameFromUrl(connection, documentUrl, index);

                // 创建 ZIP 条目
                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);

                // 读取文档内容并写入 ZIP
                inputStream = connection.getInputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    zos.write(buffer, 0, bytesRead);
                }

                zos.closeEntry();
                System.out.println("成功添加文档到 ZIP: " + fileName);

            } else {
                System.err.println("下载失败，HTTP 状态码: " + responseCode + ", URL: " + documentUrl);
            }

        } catch (IOException e) {
            System.err.println("处理文档时出错: " + documentUrl + " - " + e.getMessage());
        } finally {
            // 清理资源
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 从 HTTP 响应头或 URL 中获取文件名
     */
    private String getFileNameFromUrl(HttpURLConnection connection, String documentUrl, int index) {
        // 尝试从 Content-Disposition 头获取文件名
        String contentDisposition = connection.getHeaderField("Content-Disposition");
        if (contentDisposition != null) {
            String fileName = extractFileNameFromHeader(contentDisposition);
            if (fileName != null && !fileName.trim().isEmpty()) {
                return sanitizeFileName(fileName);
            }
        }

        // 尝试从 URL 路径获取文件名
        String fileNameFromUrl = extractFileNameFromUrl(documentUrl);
        if (fileNameFromUrl != null && !fileNameFromUrl.trim().isEmpty()) {
            return sanitizeFileName(fileNameFromUrl);
        }

        // 使用默认文件名
        String extension = getFileExtensionFromContentType(connection.getContentType());
        return "document_" + (index + 1) + extension;
    }

    /**
     * 从 Content-Disposition 头提取文件名
     */
    private String extractFileNameFromHeader(String contentDisposition) {
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            String[] parts = contentDisposition.split("filename=");
            if (parts.length > 1) {
                String fileName = parts[1].replace("\"", "").trim();
                // 处理中文文件名
                try {
                    return new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return fileName;
                }
            }
        }
        return null;
    }

    /**
     * 从 URL 路径提取文件名
     */
    private String extractFileNameFromUrl(String documentUrl) {
        try {
            URL url = new URL(documentUrl);
            String path = url.getPath();
            if (path != null && !path.isEmpty()) {
                String[] pathSegments = path.split("/");
                if (pathSegments.length > 0) {
                    String lastSegment = pathSegments[pathSegments.length - 1];
                    if (!lastSegment.isEmpty() && !lastSegment.contains("?")) {
                        return lastSegment;
                    }
                }
            }
        } catch (Exception e) {
            // 忽略异常，使用默认文件名
        }
        return null;
    }

    /**
     * 根据 Content-Type 获取文件扩展名
     */
    private String getFileExtensionFromContentType(String contentType) {
        if (contentType == null) {
            return ".bin";
        }

        switch (contentType.toLowerCase()) {
            case "application/pdf":
                return ".pdf";
            case "application/msword":
                return ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return ".docx";
            case "application/vnd.ms-excel":
                return ".xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return ".xlsx";
            case "application/vnd.ms-powerpoint":
                return ".ppt";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                return ".pptx";
            case "text/plain":
                return ".txt";
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            default:
                return ".bin";
        }
    }

    /**
     * 清理文件名，移除非法字符
     */
    private String sanitizeFileName(String fileName) {
        // 移除路径中的非法字符
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
