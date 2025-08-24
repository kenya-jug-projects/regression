package com.lifeplanner.subscriptionservice.database.entities.hullplating;
/**
 * Proprietary License Declaration
 *
 * This code is provided under a Proprietary License by THE LIFE PLANNER LTD. This means that the code
 * is not open-source, and access to it is restricted to specific individuals or entities.
 *
 * Permissions:
 * 1. You are granted a non-exclusive, non-transferable license to use this code for your internal, non-commercial purposes only, upon purchase.
 * 2. You may not redistribute, sublicense, sell, or otherwise transfer any rights to this code without explicit written permission from the LIFE PLANNER LTD.
 * 3. You may not modify, create derivative works of, or reverse engineer this code.
 *
 * Ownership:
 * All rights, title, and interest in and to the code and any accompanying documentation remain with THE LIFE PLANNER LTD.
 *
 * Warranty:
 * This code is provided "as is" without warranty of any kind. THE LIFE PLANNER LTD shall not be liable for any damages or liabilities arising from the use, misuse, or inability to use this code.
 *
 * For any inquiries regarding commercial licensing, customizations, or other permissions, please contact:
 *
 * THE LIFE PLANNER LTD
 * Email: support@thelifeplanner.co
 * Website: www.thelifeplanner.co
 *
 */
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ReleaseCandidate extends HPMetadata {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private String id;
    private String platformId;
    private String versionLabel;
    private String releaseType;
}
