# 📘 API Documentation for Angular Frontend

## ✅ Base URL
```
http://<your-backend-domain>/api
```
> Replace `<your-backend-domain>` with the actual domain or localhost:port if running locally (e.g. `http://localhost:8080/api`).

---

## 🔐 Auth Note (if applicable)
If Keycloak authentication is enabled, include a bearer token in the headers:

```ts
const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
```

---

## 📁 User Endpoints (`/api/users`)

### 🔹 Create a new user
**POST** `/users`

```ts
http.post<UserDto>('/api/users', {
  username: 'john',
  email: 'john@example.com',
  firstName: 'John',
  lastName: 'Doe',
})
```

### 🔹 Get all users
**GET** `/users`

```ts
http.get<UserDto[]>('/api/users')
```

### 🔹 Get a user by username
**GET** `/users/{username}`

```ts
http.get<UserDto>(`/api/users/${username}`)
```

### 🔹 Delete a user by username
**DELETE** `/users/{username}`

```ts
http.delete<void>(`/api/users/${username}`)
```

### 🔹 Update a user by username
**PUT** `/users/{username}`

```ts
http.put<UserDto>(`/api/users/${username}`, {
  username: 'john',
  email: 'john@example.com',
  firstName: 'John',
  lastName: 'Doe',
})
```

---

## 👥 Group Endpoints (`/api/groups`)

### 🔹 Get all groups
**GET** `/groups`

```ts
http.get<GroupDto[]>('/api/groups')
```

### 🔹 Get users in a group by group name
**GET** `/groups/name/{groupName}/users`

```ts
http.get<UserDto[]>(`/api/groups/name/${groupName}/users`)
```

---

## 🧰 Tips for Angular Setup

### Import HttpClient
Ensure `HttpClientModule` is imported in your `AppModule`.

```ts
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  imports: [
    HttpClientModule,
  ]
})
export class AppModule { }
```

### Inject and Use HttpClient
```ts
constructor(private http: HttpClient) {}
```

### Example Service Wrapper
```ts
@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = '/api/users';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(this.apiUrl);
  }

  getUser(username: string): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.apiUrl}/${username}`);
  }

  createUser(data: CreateUserDto): Observable<UserDto> {
    return this.http.post<UserDto>(this.apiUrl, data);
  }

  updateUser(username: string, data: CreateUserDto): Observable<UserDto> {
    return this.http.put<UserDto>(`${this.apiUrl}/${username}`, data);
  }

  deleteUser(username: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${username}`);
  }
}
```
