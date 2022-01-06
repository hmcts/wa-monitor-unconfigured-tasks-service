package uk.gov.hmcts.reform.wataskmonitor.config;

import io.restassured.http.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.wataskmonitor.domain.idam.UserInfo;
import uk.gov.hmcts.reform.wataskmonitor.entities.documents.Document;
import uk.gov.hmcts.reform.wataskmonitor.entities.documents.DocumentNames;
import uk.gov.hmcts.reform.wataskmonitor.services.AuthorizationHeadersProvider;
import uk.gov.hmcts.reform.wataskmonitor.services.DocumentManagementUploader;
import uk.gov.hmcts.reform.wataskmonitor.utils.BinaryResourceLoader;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static uk.gov.hmcts.reform.wataskmonitor.config.SecurityConfiguration.AUTHORIZATION;
import static uk.gov.hmcts.reform.wataskmonitor.config.SecurityConfiguration.SERVICE_AUTHORIZATION;

@Component
public class DocumentManagementFiles {

    private final Map<String, Document> storedDocuments = new ConcurrentHashMap<>();
    private Collection<Resource> documentResources;
    @Autowired
    private AuthorizationHeadersProvider authorizationHeadersProvider;
    @Autowired
    private DocumentManagementUploader documentManagementUploader;

    public void prepare() throws IOException {
        documentResources =
            BinaryResourceLoader
                .load("/documents/*")
                .values();
    }

    public Document uploadDocument(DocumentNames document) {

        Optional<Resource> maybeResource = documentResources.stream()
            .filter(res -> {
                String filename = formatFileName(res.getFilename());

                return filename.equals(document.toString());
            }).findFirst();

        if (maybeResource.isPresent()) {

            Resource documentResource = maybeResource.get();

            String filename = documentResource.getFilename().toUpperCase();

            String contentType;

            if (filename.endsWith(".PDF")) {
                contentType = "application/pdf";

            } else if (filename.endsWith(".DOC")) {
                contentType = "application/msword";

            } else if (filename.endsWith(".DOCX")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            } else {
                throw new RuntimeException("Missing content type mapping for document: " + filename);
            }

            Headers headers = authorizationHeadersProvider.getLawFirmAuthorization();
            String userToken = headers.getValue(AUTHORIZATION);
            String serviceToken = headers.getValue(SERVICE_AUTHORIZATION);
            UserInfo userInfo = authorizationHeadersProvider.getUserInfo(userToken);

            return documentManagementUploader.upload(
                documentResource,
                contentType,
                userToken,
                serviceToken,
                userInfo
            );
        } else {
            throw new IllegalStateException(
                String.format("Resource for document '{}' not found", document));
        }
    }

    public Document getDocument(DocumentNames document) {
        return uploadDocument(document);
    }

    private String formatFileName(String fileName) {
        return fileName
            .replace(".", "_")
            .replace("-", "_")
            .toUpperCase();
    }
}
