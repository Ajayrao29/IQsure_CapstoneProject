package org.hartford.iqsure.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.ClaimActionDTO;
import org.hartford.iqsure.dto.request.ClaimRequestDTO;
import org.hartford.iqsure.dto.response.ClaimResponseDTO;
import org.hartford.iqsure.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping("/users/{userId}/claims")
    public ResponseEntity<ClaimResponseDTO> submitClaim(
            @PathVariable Long userId,
            @Valid @RequestBody ClaimRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(claimService.submitClaim(userId, dto));
    }

    @GetMapping("/users/{userId}/claims")
    public ResponseEntity<List<ClaimResponseDTO>> getClaimsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(claimService.getClaimsByUser(userId));
    }

    @GetMapping("/users/{userId}/claims/policy/{userPolicyId}")
    public ResponseEntity<List<ClaimResponseDTO>> getClaimsByPolicy(
            @PathVariable Long userId,
            @PathVariable Long userPolicyId) {
        return ResponseEntity.ok(claimService.getClaimsByUserPolicy(userId, userPolicyId));
    }

    @GetMapping("/admin/claims")
    public ResponseEntity<List<ClaimResponseDTO>> getAllClaims() {
        return ResponseEntity.ok(claimService.getAllClaims());
    }

    @PutMapping("/admin/claims/{claimId}/review")
    public ResponseEntity<ClaimResponseDTO> moveToReview(@PathVariable Long claimId) {
        return ResponseEntity.ok(claimService.moveToReview(claimId));
    }

    @PutMapping("/admin/claims/{claimId}/approve")
    public ResponseEntity<ClaimResponseDTO> approveClaim(
            @PathVariable Long claimId,
            @RequestBody(required = false) ClaimActionDTO dto) {
        return ResponseEntity.ok(claimService.approveClaim(claimId, dto));
    }

    @PutMapping("/admin/claims/{claimId}/reject")
    public ResponseEntity<ClaimResponseDTO> rejectClaim(
            @PathVariable Long claimId,
            @RequestBody(required = false) ClaimActionDTO dto) {
        return ResponseEntity.ok(claimService.rejectClaim(claimId, dto));
    }

    @PostMapping(value = "/claims/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadClaimDocument(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is empty"));
        }

        String contentType = file.getContentType();
        if (contentType == null || (
                !contentType.equals("application/pdf") &&
                        !contentType.equals("image/png") &&
                        !contentType.equals("image/jpeg")
        )) {
            return ResponseEntity.badRequest().body(Map.of("message", "Only PDF, PNG, JPG files are allowed"));
        }

        String uploadsDir = "uploads/claims";
        Path uploadPath = Paths.get(uploadsDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";

        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex);
        }

        String fileName = UUID.randomUUID() + extension;
        Path targetPath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok(Map.of(
                "fileName", fileName,
                "documentUrl", "/uploads/claims/" + fileName
        ));
    }
}