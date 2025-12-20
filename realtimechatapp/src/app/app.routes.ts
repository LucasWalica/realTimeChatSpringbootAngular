import { Routes } from '@angular/router';

export const routes: Routes = [
    {path: "login", loadComponent: () => import("./components/auth/login/login").then(m=>m.LoginComponent)},
    {path: "register", loadComponent: () => import("./components/auth/register/register").then(m=>m.RegisterComponent)},
    {path: "chat-list", loadComponent: () => import("./components/chat-list/chat-list").then(m=>m.ChatList)},
    {path: "chat", loadComponent: () => import("./components/chat-detail/chat-detail").then(m=>m.ChatDetail)},
    {path: "**", redirectTo: "login"}
];
