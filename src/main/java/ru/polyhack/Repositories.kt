package ru.polyhack

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRep: JpaRepository<User, Int>

@Repository
interface ProjectRep: JpaRepository<Project, Long>

@Repository
interface LogRep: JpaRepository<Log, Long>