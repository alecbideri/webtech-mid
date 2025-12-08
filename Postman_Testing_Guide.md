# Postman Testing Guide

This guide details how to test the FindJobApp backend services using Postman.

## Prerequisites
- Postman installed.
- Application running on `http://localhost:8080`.

## Authentication Method
This API uses **Basic Authentication**.
- In Postman, go to the **Authorization** tab for request.
- Select **Type**: `Basic Auth`.
- Enter `Username` and `Password` of the user you are testing as.

## Test Scenarios

### 1. Registration
**Endpoint**: `POST /api/auth/register`
**Auth**: None (Public)
**Body (JSON)**:
**Job Creator**:
```json
{
    "username": "creator1",
    "email": "creator1@test.com",
    "password": "password123",
    "name": "John Creator",
    "role": "JOB_CREATOR"
}
```
**Job Seeker**:
```json
{
    "username": "seeker1",
    "email": "seeker1@test.com",
    "password": "password123",
    "name": "Jane Seeker",
    "role": "JOB_SEEKER"
}
```
**Sysadmin** (You might need to seed one manually or register one if allowed):
```json
{
    "username": "admin",
    "email": "admin@test.com",
    "password": "adminpass",
    "name": "Super Admin",
    "role": "SYSADMIN"
}
```

### 2. Sysadmin Verification (Required for Creator)
**Endpoint**: `PUT /api/admin/users/{id}/verify`
**Auth**: Basic Auth (Log in as Sysadmin)
**Effect**: Allows the Job Creator to post jobs.

### 3. Job Management (Creator)
**Endpoint**: `POST /api/jobs`
**Auth**: Basic Auth (Log in as Verified Creator)
**Body**:
```json
{
    "title": "Software Engineer",
    "description": "Develop amazing apps.",
    "location": "Remote",
    "type": "Full-time"
}
```

### 4. Viewing Jobs (Seeker)
**Endpoint**: `GET /api/jobs`
**Auth**: Basic Auth (Log in as Seeker)

### 5. Applying for a Job (Seeker)
**Endpoint**: `POST /api/applications/{jobId}`
**Auth**: Basic Auth (Log in as Seeker)

### 6. Process Application (Creator)
**Endpoint**: `PUT /api/applications/{applicationId}/status`
**Auth**: Basic Auth (Log in as Creator of the job)
**Body**:
```json
{
    "status": "CONFIRMED"
}
```
**Note**: This triggers the email service. Check console logs if mock email is used.

### 7. Sysadmin Dashboard
**Endpoint**: `GET /api/admin/dashboard`
**Auth**: Basic Auth (Log in as Sysadmin)
**Response**:
```json
{
    "totalUsers": 5,
    "totalJobs": 2,
    "jobCreators": 2,
    ...
}
```

## Testing Flow Summary
1. **Register** a Sysadmin, a Job Creator, and a Job Seeker.
2. **Login as Sysadmin** and call verify endpoint for the Creator's ID.
3. **Login as Creator** and post a Job.
4. **Login as Seeker**, list jobs, find the ID of the new job, and apply.
5. **Login as Creator**, view applications, and change status to `CONFIRMED`.
6. **Login as Sysadmin** and check the dashboard.
