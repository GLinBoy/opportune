package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.InterviewNote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InterviewNoteRepository : JpaRepository<InterviewNote, UUID>

