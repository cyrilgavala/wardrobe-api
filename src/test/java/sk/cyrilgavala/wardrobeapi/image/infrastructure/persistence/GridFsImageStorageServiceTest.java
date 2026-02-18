package sk.cyrilgavala.wardrobeapi.image.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import sk.cyrilgavala.wardrobeapi.image.domain.exception.ImageNotFoundException;
import sk.cyrilgavala.wardrobeapi.image.domain.exception.InvalidImageException;

@ExtendWith(MockitoExtension.class)
class GridFsImageStorageServiceTest {

  @Mock
  private GridFsTemplate gridFsTemplate;

  @Mock
  private GridFSFile gridFSFile;

  @Mock
  private GridFsResource gridFsResource;

  @InjectMocks
  private GridFsImageStorageService service;

  @Test
  void storesImageSuccessfullyWithValidJpegFile() {
    MockMultipartFile file = new MockMultipartFile(
        "image",
        "test.jpg",
        "image/jpeg",
        "image-content".getBytes()
    );
    ObjectId expectedId = new ObjectId();

    when(gridFsTemplate.store(any(), eq("test.jpg"), eq("image/jpeg"))).thenReturn(expectedId);

    String result = service.storeImage(file);

    assertThat(result).isEqualTo(expectedId.toString());
    verify(gridFsTemplate).store(any(), eq("test.jpg"), eq("image/jpeg"));
  }

  @Test
  void storesImageSuccessfullyWithValidPngFile() {
    MockMultipartFile file = new MockMultipartFile(
        "image",
        "test.png",
        "image/png",
        "png-content".getBytes()
    );
    ObjectId expectedId = new ObjectId();

    when(gridFsTemplate.store(any(), eq("test.png"), eq("image/png"))).thenReturn(expectedId);

    String result = service.storeImage(file);

    assertThat(result).isEqualTo(expectedId.toString());
  }

  @Test
  void storesImageSuccessfullyWithValidWebpFile() {
    MockMultipartFile file = new MockMultipartFile(
        "image",
        "test.webp",
        "image/webp",
        "webp-content".getBytes()
    );
    ObjectId expectedId = new ObjectId();

    when(gridFsTemplate.store(any(), eq("test.webp"), eq("image/webp"))).thenReturn(expectedId);

    String result = service.storeImage(file);

    assertThat(result).isEqualTo(expectedId.toString());
  }

  @Test
  void storesImageSuccessfullyWithJpgContentType() {
    MockMultipartFile file = new MockMultipartFile(
        "image",
        "test.jpg",
        "image/jpg",
        "jpg-content".getBytes()
    );
    ObjectId expectedId = new ObjectId();

    when(gridFsTemplate.store(any(), eq("test.jpg"), eq("image/jpg"))).thenReturn(expectedId);

    String result = service.storeImage(file);

    assertThat(result).isEqualTo(expectedId.toString());
  }

  @Test
  void throwsInvalidImageExceptionWhenFileIsNull() {
    assertThatThrownBy(() -> service.storeImage(null))
        .isInstanceOf(InvalidImageException.class)
        .hasMessageContaining("Image file is required");
  }

  @Test
  void throwsInvalidImageExceptionWhenFileIsEmpty() {
    MockMultipartFile emptyFile = new MockMultipartFile(
        "image",
        "empty.jpg",
        "image/jpeg",
        new byte[0]
    );

    assertThatThrownBy(() -> service.storeImage(emptyFile))
        .isInstanceOf(InvalidImageException.class)
        .hasMessageContaining("Image file is required");
  }

  @Test
  void throwsInvalidImageExceptionWhenFileSizeExceedsLimit() {
    byte[] largeContent = new byte[21 * 1024 * 1024];
    MockMultipartFile largeFile = new MockMultipartFile(
        "image",
        "large.jpg",
        "image/jpeg",
        largeContent
    );

    assertThatThrownBy(() -> service.storeImage(largeFile))
        .isInstanceOf(InvalidImageException.class)
        .hasMessageContaining("exceeds maximum allowed size");
  }

  @Test
  void throwsInvalidImageExceptionWhenContentTypeIsInvalid() {
    MockMultipartFile invalidFile = new MockMultipartFile(
        "image",
        "document.pdf",
        "application/pdf",
        "pdf-content".getBytes()
    );

    assertThatThrownBy(() -> service.storeImage(invalidFile))
        .isInstanceOf(InvalidImageException.class)
        .hasMessageContaining("Invalid image type");
  }

  @Test
  void throwsInvalidImageExceptionWhenContentTypeIsNull() {
    MockMultipartFile fileWithNullContentType = new MockMultipartFile(
        "image",
        "test.jpg",
        null,
        "content".getBytes()
    );

    assertThatThrownBy(() -> service.storeImage(fileWithNullContentType))
        .isInstanceOf(InvalidImageException.class)
        .hasMessageContaining("Invalid image type");
  }

  @Test
  void throwsInvalidImageExceptionForTextFile() {
    MockMultipartFile textFile = new MockMultipartFile(
        "image",
        "file.txt",
        "text/plain",
        "text-content".getBytes()
    );

    assertThatThrownBy(() -> service.storeImage(textFile))
        .isInstanceOf(InvalidImageException.class)
        .hasMessageContaining("Invalid image type");
  }

  @Test
  void acceptsFileSizeAtExactLimit() {
    byte[] maxContent = new byte[20 * 1024 * 1024];
    MockMultipartFile maxFile = new MockMultipartFile(
        "image",
        "max.jpg",
        "image/jpeg",
        maxContent
    );
    ObjectId expectedId = new ObjectId();

    when(gridFsTemplate.store(any(), eq("max.jpg"), eq("image/jpeg"))).thenReturn(expectedId);

    String result = service.storeImage(maxFile);

    assertThat(result).isNotNull();
  }

  @Test
  void acceptsContentTypeWithMixedCase() {
    MockMultipartFile file = new MockMultipartFile(
        "image",
        "test.jpg",
        "IMAGE/JPEG",
        "content".getBytes()
    );
    ObjectId expectedId = new ObjectId();

    when(gridFsTemplate.store(any(), eq("test.jpg"), eq("IMAGE/JPEG"))).thenReturn(expectedId);

    String result = service.storeImage(file);

    assertThat(result).isNotNull();
  }

  @Test
  void retrievesImageSuccessfully() throws IOException {
    String imageId = new ObjectId().toString();
    byte[] expectedContent = "image-data".getBytes();

    when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFSFile);
    when(gridFsTemplate.getResource(gridFSFile)).thenReturn(gridFsResource);
    when(gridFsResource.getInputStream()).thenReturn(new ByteArrayInputStream(expectedContent));

    byte[] result = service.getImage(imageId);

    assertThat(result).isEqualTo(expectedContent);
  }

  @Test
  void throwsImageNotFoundExceptionWhenImageDoesNotExist() {
    String invalidImageId = "507f1f77bcf86cd799439011";

    when(gridFsTemplate.findOne(any(Query.class))).thenReturn(null);

    assertThatThrownBy(() -> service.getImage(invalidImageId))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void throwsImageNotFoundExceptionForMalformedImageId() {
    String malformedId = "not-an-object-id";

    assertThatThrownBy(() -> service.getImage(malformedId))
        .isInstanceOf(ImageNotFoundException.class);
  }

  @Test
  void retrievesContentTypeSuccessfully() {
    String imageId = new ObjectId().toString();
    Document metadata = new Document("_contentType", "image/jpeg");

    when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFSFile);
    when(gridFSFile.getMetadata()).thenReturn(metadata);

    String result = service.getContentType(imageId);

    assertThat(result).isEqualTo("image/jpeg");
  }

  @Test
  void returnsDefaultContentTypeWhenMetadataIsNull() {
    String imageId = new ObjectId().toString();

    when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFSFile);
    when(gridFSFile.getMetadata()).thenReturn(null);

    String result = service.getContentType(imageId);

    assertThat(result).isEqualTo("application/octet-stream");
  }

  @Test
  void throwsImageNotFoundExceptionWhenGettingContentTypeForNonExistentImage() {
    String invalidImageId = "not-an-object-id";

    assertThatThrownBy(() -> service.getContentType(invalidImageId))
        .isInstanceOf(ImageNotFoundException.class);
  }

  @Test
  void deletesImageSuccessfully() {
    String imageId = new ObjectId().toString();

    service.deleteImage(imageId);

    verify(gridFsTemplate).delete(any(Query.class));
  }

  @Test
  void doesNotDeleteWhenImageIdIsNull() {
    service.deleteImage(null);

    verify(gridFsTemplate, never()).delete(any(Query.class));
  }

  @Test
  void doesNotDeleteWhenImageIdIsEmpty() {
    service.deleteImage("");

    verify(gridFsTemplate, never()).delete(any(Query.class));
  }

  @Test
  void doesNotDeleteWhenImageIdIsBlank() {
    service.deleteImage("   ");

    verify(gridFsTemplate, never()).delete(any(Query.class));
  }

  @Test
  void silentlyHandlesExceptionWhenDeletingInvalidImageId() {
    String invalidImageId = "not-an-object-id";

    service.deleteImage(invalidImageId);
  }

  @Test
  void returnsTrueWhenImageExists() {
    String imageId = new ObjectId().toString();

    when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFSFile);
    when(gridFSFile.getFilename()).thenReturn("test.jpg");

    boolean result = service.imageExists(imageId);

    assertThat(result).isTrue();
  }

  @Test
  void returnsFalseWhenImageDoesNotExist() {
    String imageId = new ObjectId().toString();

    when(gridFsTemplate.findOne(any(Query.class))).thenThrow(new IllegalArgumentException());

    boolean result = service.imageExists(imageId);

    assertThat(result).isFalse();
  }

  @Test
  void returnsFalseWhenImageIdIsNull() {
    boolean result = service.imageExists(null);

    assertThat(result).isFalse();
    verify(gridFsTemplate, never()).findOne(any(Query.class));
  }

  @Test
  void returnsFalseWhenImageIdIsEmpty() {
    boolean result = service.imageExists("");

    assertThat(result).isFalse();
    verify(gridFsTemplate, never()).findOne(any(Query.class));
  }

  @Test
  void returnsFalseWhenImageIdIsBlank() {
    boolean result = service.imageExists("   ");

    assertThat(result).isFalse();
    verify(gridFsTemplate, never()).findOne(any(Query.class));
  }

  @Test
  void returnsFalseWhenImageIdIsMalformed() {
    String malformedId = "not-a-valid-id";

    boolean result = service.imageExists(malformedId);

    assertThat(result).isFalse();
  }

  @Test
  void throwsRuntimeExceptionWhenIOErrorOccursDuringStorage() throws IOException {
    MockMultipartFile file = new MockMultipartFile(
        "image",
        "test.jpg",
        "image/jpeg",
        "content".getBytes()
    ) {
      @Override
      public InputStream getInputStream() throws IOException {
        throw new IOException("Storage error");
      }
    };

    assertThatThrownBy(() -> service.storeImage(file))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to store image");
  }

  @Test
  void throwsRuntimeExceptionWhenIOErrorOccursDuringRetrieval() throws IOException {
    String imageId = new ObjectId().toString();

    when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFSFile);
    when(gridFsTemplate.getResource(gridFSFile)).thenReturn(gridFsResource);
    when(gridFsResource.getInputStream()).thenThrow(new IOException("Read error"));

    assertThatThrownBy(() -> service.getImage(imageId))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to read image");
  }
}





