package com.taskmanager.repository;

import com.taskmanager.model.*;
import com.taskmanager.model.Task.TaskStatus;
import com.taskmanager.model.Task.Priority;
import com.taskmanager.model.Project.ProjectStatus;
import com.taskmanager.model.Role.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
