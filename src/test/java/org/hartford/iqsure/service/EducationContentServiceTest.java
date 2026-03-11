package org.hartford.iqsure.service;

import org.hartford.iqsure.dto.response.EducationContentDTO;
import org.hartford.iqsure.entity.EducationContent;
import org.hartford.iqsure.repository.EducationContentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EducationContentServiceTest {

    @Mock
    private EducationContentRepository repositoryMock;

    @InjectMocks
    private EducationContentService educationContentService;

    @Test
    public void testGetByLanguage() {
        // Arrange
        EducationContent content = EducationContent.builder()
                .id(1L)
                .topic("insurance")
                .language("en")
                .title("Insurance Info")
                .content("Basic content")
                .build();
        when(repositoryMock.findByLanguage("en")).thenReturn(java.util.List.of(content));

        // Act
        List<EducationContentDTO> result = educationContentService.getByLanguage("en");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Insurance Info", result.get(0).getTitle());
        verify(repositoryMock).findByLanguage("en");
    }

    @Test
    public void testGetByTopicAndLanguage() {
        // Arrange
        EducationContent content = EducationContent.builder()
                .id(1L)
                .topic("insurance")
                .language("en")
                .title("Insurance Info")
                .content("Basic content")
                .build();
        when(repositoryMock.findByTopicAndLanguage("insurance", "en")).thenReturn(Optional.of(content));

        // Act
        EducationContentDTO result = educationContentService.getByTopicAndLanguage("insurance", "en");

        // Assert
        assertNotNull(result);
        assertEquals("Insurance Info", result.getTitle());
        verify(repositoryMock).findByTopicAndLanguage("insurance", "en");
    }
}
