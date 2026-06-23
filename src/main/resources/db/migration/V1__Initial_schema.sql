-- Create Profile table
CREATE TABLE profile (
    id UUID NOT NULL PRIMARY KEY,
    email VARCHAR(255),
    forename VARCHAR(255),
    surname VARCHAR(255),
    password VARCHAR(255),
    job_title VARCHAR(255),
    location VARCHAR(255),
    phone VARCHAR(30),
    linkedin_url VARCHAR(500),
    portfolio_url VARCHAR(500),
    professional_summary TEXT,
    avatar VARCHAR(255),
    email_verification BOOLEAN,
    last_login TIMESTAMP,
    status VARCHAR(50) CHECK (status IN ('ACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION')),
    subscription VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_profile_email UNIQUE (email)
);

-- Create profile_role table for storing user roles
CREATE TABLE profile_role (
    profile_id UUID NOT NULL,
    role VARCHAR(255) NOT NULL,
    CONSTRAINT fk_profile_role_profile FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, role)
);

-- Create ValidationKey table
CREATE TABLE verification_code (
    id UUID NOT NULL PRIMARY KEY,
    type VARCHAR(50),
    profile_id UUID,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_verification_code_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create Session table
-- refresh_token_id is the primary key (indexed automatically)
-- access_token_id has a unique index for O(1) lookup by access token
CREATE TABLE session (
    refresh_token_id          UUID         NOT NULL PRIMARY KEY,
    access_token_id           UUID         NOT NULL,
    access_token_expiration   TIMESTAMP    NOT NULL,
    refresh_token_expiration  TIMESTAMP    NOT NULL,
    status                    VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'EXPIRED', 'REVOKED')),
    last_active_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    client_agent              VARCHAR(255) NOT NULL,
    client_ip                 VARCHAR(255) NOT NULL,
    client_geo                VARCHAR(255),
    device_id                 VARCHAR(255),
    device_type               VARCHAR(255),
    os                        VARCHAR(255),
    browser                   VARCHAR(255),
    is_mobile                 BOOLEAN      NOT NULL DEFAULT FALSE,
    login_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at                TIMESTAMP,
    revocation_reason         VARCHAR(50)  CHECK (revocation_reason IN (
                                  'USER_INITIATED', 'USER_ALL_SESSIONS', 'PASSWORD_CHANGE', 'PASSWORD_RESET',
                                  'SUSPICIOUS_ACTIVITY', 'ADMIN_ACTION', 'ACCOUNT_SUSPENDED',
                                  'DEVICE_LOST_OR_STOLEN', 'SECURITY_INCIDENT', 'OTHER'
                              )),
    last_refreshed_at         TIMESTAMP,
    profile_id                UUID         NOT NULL,
    CONSTRAINT fk_session_profile FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE
);

-- Create Company table
CREATE TABLE company (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    industry VARCHAR(255),
    website VARCHAR(255),
    company_size VARCHAR(255),
    location VARCHAR(255),
    founded_year VARCHAR(255),
    description TEXT,
    note TEXT,
    logo VARCHAR(255),
    status VARCHAR(50) CHECK (status IN ('INTERESTED', 'NOT_INTERESTED', 'BLOCKED')),
    profile_id UUID,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_company_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create Application table
CREATE TABLE application (
    id UUID NOT NULL PRIMARY KEY,
    url VARCHAR(255),
    title VARCHAR(255),
    location VARCHAR(255),
    applied_at TIMESTAMP,
    salary VARCHAR(255),
    note TEXT,
    raw_content TEXT,
    description TEXT,
    cover_letter TEXT,
    resume_insights TEXT,
    status VARCHAR(50) CHECK (status IN ('INITIATED', 'AI_PROCESSING', 'READY_TO_APPLY', 'APPLIED', 'IN_PROGRESS', 'REJECTED', 'OFFER_RECEIVED', 'ACCEPTED', 'DECLINED')),
    interview_introduction TEXT,
    resume_overall_score INTEGER CHECK (resume_overall_score BETWEEN 0 AND 100),
    skill_match_score INTEGER CHECK (skill_match_score BETWEEN 0 AND 100),
    skill_match_rationale TEXT,
    experience_match_score INTEGER CHECK (experience_match_score BETWEEN 0 AND 100),
    experience_match_rationale TEXT,
    education_match_score INTEGER CHECK (education_match_score BETWEEN 0 AND 100),
    education_match_rationale TEXT,
    keyword_match_score INTEGER CHECK (keyword_match_score BETWEEN 0 AND 100),
    keyword_match_rationale TEXT,
    company_id UUID,
    profile_id UUID,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_application_company FOREIGN KEY (company_id) REFERENCES company(id),
    CONSTRAINT fk_application_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create ApplicationTimeline table
CREATE TABLE application_timeline (
    id UUID NOT NULL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    status VARCHAR(50) CHECK (status IN ('INITIATED', 'APPLIED', 'IN_PROGRESS', 'REJECTED', 'OFFER_RECEIVED', 'ACCEPTED', 'DECLINED')),
    occurred_at TIMESTAMP,
    application_id UUID,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_application_timeline_application FOREIGN KEY (application_id) REFERENCES application(id)
);

-- Create InterviewNote table
CREATE TABLE interview_note (
    id UUID NOT NULL PRIMARY KEY,
    date TIMESTAMP,
    notes TEXT,
    application_id UUID,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_interview_note_application FOREIGN KEY (application_id) REFERENCES application(id)
);

-- Create ProfileResume table (extends Attachment with TABLE_PER_CLASS inheritance)
CREATE TABLE profile_resume (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    path VARCHAR(255),
    content_type VARCHAR(255),
    content_length BIGINT,
    profile_id UUID,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_profile_resume_profile FOREIGN KEY (profile_id) REFERENCES profile(id),
    CONSTRAINT uk_profile_resume_profile UNIQUE (profile_id)
);

-- Create WorkExperience table
CREATE TABLE work_experience (
    id UUID NOT NULL PRIMARY KEY,
    job_title VARCHAR(200) NOT NULL,
    company VARCHAR(200) NOT NULL,
    location VARCHAR(200),
    start_month SMALLINT,
    start_year SMALLINT,
    end_month SMALLINT,
    end_year SMALLINT,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    profile_id UUID NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_work_experience_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create WorkExperienceBullet table
CREATE TABLE work_experience_bullet (
    id UUID NOT NULL PRIMARY KEY,
    content TEXT NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    work_experience_id UUID NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_work_experience_bullet_work_experience FOREIGN KEY (work_experience_id) REFERENCES work_experience(id) ON DELETE CASCADE
);

-- Create Education table
CREATE TABLE education (
    id UUID NOT NULL PRIMARY KEY,
    school VARCHAR(200) NOT NULL,
    degree VARCHAR(200) NOT NULL,
    field_of_study VARCHAR(200) NOT NULL,
    start_month SMALLINT,
    start_year SMALLINT,
    end_month SMALLINT,
    end_year SMALLINT,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    gpa VARCHAR(10),
    honors VARCHAR(200),
    display_order INT NOT NULL DEFAULT 0,
    profile_id UUID NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_education_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create EducationCourse table
CREATE TABLE education_course (
    education_id UUID NOT NULL,
    course_name VARCHAR(200) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_education_course_education FOREIGN KEY (education_id) REFERENCES education(id) ON DELETE CASCADE
);

-- Create SkillGroup table
CREATE TABLE skill_group (
    id UUID NOT NULL PRIMARY KEY,
    category VARCHAR(100) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    profile_id UUID NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_skill_group_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create SkillItem table
CREATE TABLE skill_item (
    skill_group_id UUID NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_skill_item_skill_group FOREIGN KEY (skill_group_id) REFERENCES skill_group(id) ON DELETE CASCADE
);

-- Create ResumeProject table
CREATE TABLE resume_project (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    url VARCHAR(500),
    start_month SMALLINT,
    start_year SMALLINT,
    end_month SMALLINT,
    end_year SMALLINT,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    profile_id UUID NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_resume_project_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create ResumeProjectTech table
CREATE TABLE resume_project_tech (
    resume_project_id UUID NOT NULL,
    technology VARCHAR(100) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_resume_project_tech_resume_project FOREIGN KEY (resume_project_id) REFERENCES resume_project(id) ON DELETE CASCADE
);

-- Create ResumeCertification table
CREATE TABLE resume_certification (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    issuing_organization VARCHAR(200),
    issue_date TIMESTAMP,
    expiration_date TIMESTAMP,
    credential_id VARCHAR(100),
    credential_url VARCHAR(500),
    display_order INT NOT NULL DEFAULT 0,
    profile_id UUID NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_resume_certification_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create ResumeLanguage table
CREATE TABLE resume_language (
    id UUID NOT NULL PRIMARY KEY,
    language VARCHAR(100) NOT NULL,
    proficiency VARCHAR(20) NOT NULL CHECK (proficiency IN ('NATIVE', 'FLUENT', 'CONVERSATIONAL', 'BASIC')),
    display_order INT NOT NULL DEFAULT 0,
    profile_id UUID NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_resume_language_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create VolunteerWork table
CREATE TABLE volunteer_work (
    id UUID NOT NULL PRIMARY KEY,
    role VARCHAR(200) NOT NULL,
    organization VARCHAR(200) NOT NULL,
    location VARCHAR(200),
    start_month SMALLINT,
    start_year SMALLINT,
    end_month SMALLINT,
    end_year SMALLINT,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    display_order INT NOT NULL DEFAULT 0,
    profile_id UUID NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_volunteer_work_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create ResumePublication table
CREATE TABLE resume_publication (
    id UUID NOT NULL PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    publisher VARCHAR(200),
    publication_date TIMESTAMP,
    url VARCHAR(500),
    description TEXT,
    display_order INT NOT NULL DEFAULT 0,
    profile_id UUID NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_resume_publication_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create ResumeAward table
CREATE TABLE resume_award (
    id UUID NOT NULL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    issuer VARCHAR(200),
    award_date TIMESTAMP,
    description TEXT,
    display_order INT NOT NULL DEFAULT 0,
    profile_id UUID NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_resume_award_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create ProfessionalAffiliation table
CREATE TABLE professional_affiliation (
    id UUID NOT NULL PRIMARY KEY,
    organization VARCHAR(200) NOT NULL,
    role VARCHAR(200),
    start_year SMALLINT,
    end_year SMALLINT,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    display_order INT NOT NULL DEFAULT 0,
    profile_id UUID NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_professional_affiliation_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create ApplicationResume table (extends Attachment with TABLE_PER_CLASS inheritance)
CREATE TABLE application_resume (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    path VARCHAR(255),
    content_type VARCHAR(255),
    content_length BIGINT,
    application_id UUID,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_application_resume_application FOREIGN KEY (application_id) REFERENCES application(id),
    CONSTRAINT uk_application_resume_application UNIQUE (application_id)
);

-- Create ApplicationAttachment table (extends Attachment with TABLE_PER_CLASS inheritance)
CREATE TABLE application_attachment (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    path VARCHAR(255),
    content_type VARCHAR(255),
    content_length BIGINT,
    application_id UUID,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_application_attachment_application FOREIGN KEY (application_id) REFERENCES application(id)
);

-- Create InterviewAttachment table (extends Attachment with TABLE_PER_CLASS inheritance)
CREATE TABLE interview_attachment (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    path VARCHAR(255),
    content_type VARCHAR(255),
    content_length BIGINT,
    interview_note_id UUID,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_interview_attachment_interview_note FOREIGN KEY (interview_note_id) REFERENCES interview_note(id)
);

-- Create CompanyMetaData table with composite primary key
CREATE TABLE company_meta_data (
    id UUID NOT NULL PRIMARY KEY,
    company_id UUID NOT NULL,
    meta_name VARCHAR(255) NOT NULL,
    meta_value TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_company_meta_data_company FOREIGN KEY (company_id) REFERENCES company(id)
);

-- Create ApplicationMetaData table with composite primary key
CREATE TABLE application_meta_data (
    id UUID NOT NULL PRIMARY KEY,
    application_id UUID NOT NULL,
    meta_name VARCHAR(255) NOT NULL,
    meta_value TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_application_meta_data_application FOREIGN KEY (application_id) REFERENCES application(id)
);

-- Create indexes for better performance
CREATE INDEX idx_profile_email ON profile(email);
CREATE INDEX idx_profile_status ON profile(status);
CREATE INDEX idx_profile_role_profile_id ON profile_role(profile_id);
CREATE INDEX idx_verification_code_profile ON verification_code(profile_id);
CREATE INDEX idx_verification_code_type ON verification_code(type);
CREATE INDEX idx_company_name ON company(name);
CREATE INDEX idx_company_status ON company(status);
CREATE INDEX idx_company_industry ON company(industry);
CREATE INDEX idx_application_status ON application(status);
CREATE INDEX idx_application_company ON application(company_id);
CREATE INDEX idx_application_profile ON application(profile_id);
CREATE INDEX idx_application_applied_at ON application(applied_at);
CREATE INDEX idx_application_timeline_application ON application_timeline(application_id);
CREATE INDEX idx_application_timeline_status ON application_timeline(status);
CREATE INDEX idx_application_timeline_occurred_at ON application_timeline(occurred_at);
CREATE INDEX idx_interview_note_application ON interview_note(application_id);
CREATE INDEX idx_interview_note_date ON interview_note(date);
CREATE INDEX idx_profile_resume_profile ON profile_resume(profile_id);
CREATE INDEX idx_application_resume_application ON application_resume(application_id);
CREATE INDEX idx_application_attachment_application ON application_attachment(application_id);
CREATE INDEX idx_interview_attachment_interview_note ON interview_attachment(interview_note_id);
CREATE INDEX idx_company_meta_data_meta_name ON company_meta_data(meta_name);
CREATE INDEX idx_application_meta_data_meta_name ON application_meta_data(meta_name);

-- Core resume data indexes
CREATE INDEX idx_work_experience_profile ON work_experience(profile_id);
CREATE INDEX idx_work_experience_bullet_work_experience ON work_experience_bullet(work_experience_id);
CREATE INDEX idx_education_profile ON education(profile_id);
CREATE INDEX idx_education_course_education ON education_course(education_id);
CREATE INDEX idx_skill_group_profile ON skill_group(profile_id);
CREATE INDEX idx_skill_item_skill_group ON skill_item(skill_group_id);

-- Optional resume section indexes
CREATE INDEX idx_resume_project_profile ON resume_project(profile_id);
CREATE INDEX idx_resume_project_tech_resume_project ON resume_project_tech(resume_project_id);
CREATE INDEX idx_resume_certification_profile ON resume_certification(profile_id);
CREATE INDEX idx_resume_language_profile ON resume_language(profile_id);
CREATE INDEX idx_volunteer_work_profile ON volunteer_work(profile_id);
CREATE INDEX idx_resume_publication_profile ON resume_publication(profile_id);
CREATE INDEX idx_resume_award_profile ON resume_award(profile_id);
CREATE INDEX idx_professional_affiliation_profile ON professional_affiliation(profile_id);

-- Session indexes
-- Unique index on access_token_id: O(1) lookup / existence check by access token
CREATE UNIQUE INDEX idx_session_access_token_id       ON session(access_token_id);
-- Profile sessions: fast retrieval of all sessions for a user
CREATE INDEX idx_session_profile_id                   ON session(profile_id);
-- Active sessions per user: most common query (auth validation path)
CREATE INDEX idx_session_profile_status               ON session(profile_id, status);
-- Token expiry maintenance: sweep expired rows efficiently
CREATE INDEX idx_session_refresh_token_expiration     ON session(refresh_token_expiration);
CREATE INDEX idx_session_access_token_expiration      ON session(access_token_expiration);
-- Status-only: bulk revocation / audit queries
CREATE INDEX idx_session_status                       ON session(status);

-- System settings singleton table.
-- Always contains exactly one row (id = 00000000-0000-0000-0000-000000000001).
-- Admins read/update this row via GET/PATCH /api/admin/settings.

CREATE TABLE system_settings (
    id                           UUID         NOT NULL PRIMARY KEY,
    registration_enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
    ai_scoring_enabled           BOOLEAN      NOT NULL DEFAULT TRUE,
    email_notifications_enabled  BOOLEAN      NOT NULL DEFAULT TRUE,
    maintenance_mode             BOOLEAN      NOT NULL DEFAULT FALSE,
    max_applications_per_user    INTEGER      NOT NULL DEFAULT 100,
    max_ai_requests_per_day      INTEGER      NOT NULL DEFAULT 1000,
    created_date                 TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    last_modified_date           TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- Seed the single settings row with safe production defaults
INSERT INTO system_settings (
    id,
    registration_enabled,
    ai_scoring_enabled,
    email_notifications_enabled,
    maintenance_mode,
    max_applications_per_user,
    max_ai_requests_per_day
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    TRUE,
    TRUE,
    TRUE,
    FALSE,
    100,
    1000
);

