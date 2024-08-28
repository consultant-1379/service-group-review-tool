import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdminComponent } from "./rmrt-app/admin/admin.component";
import { RepositoryViewComponent } from "./rmrt-app/views/repositories-view/repository-view/repository-view.component";
import { RepositoriesViewComponent } from "./rmrt-app/views/repositories-view/repositories-view.component";
import { GenericLoadDriversViewComponent} from "./rmrt-app/views/generic-load-drivers-view/generic-load-drivers-view.component";
import { ReferenceDeploymentsViewComponent } from "./rmrt-app/views/reference-deployments-view/reference-deployments-view.component";
import { FileSystemsViewComponent } from "./rmrt-app/views/file-systems-view/file-systems-view.component";
import { KeyDimensioningValuesViewComponent } from "./rmrt-app/views/key-dimensioning-values-view/key-dimensioning-values-view.component";
import { HomeViewComponent } from "./rmrt-app/views/home-view/home-view.component";
import { RepositoryAdminComponent } from "./rmrt-app/admin/repository-admin/repository-admin.component";
import { GenericLoadDriversAdminComponent } from "./rmrt-app/admin/generic-load-drivers-admin/generic-load-drivers-admin.component";
import { ReferenceDeploymentsAdminComponent } from "./rmrt-app/admin/reference-deployments-admin/reference-deployments-admin.component";
import { FileSystemsAdminComponent } from "./rmrt-app/admin/file-systems-admin/file-systems-admin.component";
import { AdminLoginComponent } from "./rmrt-app/views/admin-login/admin-login.component";
import { DeploymentDependantLoadDriversComponent } from "./rmrt-app/admin/deployment-dependant-load-drivers/deployment-dependant-load-drivers.component";
import { UserRolesAdminComponent } from "./rmrt-app/admin/user-roles-admin/user-roles-admin.component";
import { RmrtAppComponent } from "./rmrt-app/rmrt-app.component";
import { ReadyReckonerAppComponent } from "./ready-reckoner-app/ready-reckoner-app.component";
import { CatalogComponent } from "./ready-reckoner-app/components/views/catalog-view/catalog.component";
import { NetworkElementSearchFormComponent } from "./ready-reckoner-app/components/views/network-element-search-form/network-element-search-form.component";
import { ReferenceDeploymentSizesComponent } from "./ready-reckoner-app/components/views/reference-deployment-sizes/reference-deployment-sizes.component";
import { CanDeactivateGuard } from "./ready-reckoner-app/guards/can-deactivate.guard";
import { DeploymentTypesAdminComponent } from "./rmrt-app/admin/deployment-types-admin/deployment-types-admin.component";

const routes: Routes = [
  {
    path: '',
    component: RmrtAppComponent,
    children: [
      {
        path: '',
        component: RepositoriesViewComponent
      },
      {
        path: 'view',
        children: [
          {
            path: '',
            component: RepositoriesViewComponent
          },
          {
            path: 'login',
            component: AdminLoginComponent
          },
          {
            path: 'home',
            component: HomeViewComponent
          },
          {
            path: 'repositories',
            component: RepositoriesViewComponent
          },
          {
            path: 'repository',
            component: RepositoryViewComponent
          },
          {
            path: 'genericLoadDrivers',
            component: GenericLoadDriversViewComponent
          },
          {
            path: 'referenceDeployments',
            component: ReferenceDeploymentsViewComponent
          },
          {
            path: 'fileSystems',
            component: FileSystemsViewComponent
          },
          {
            path: 'keyDimensioningValues',
            component: KeyDimensioningValuesViewComponent
          },
          {
            path: 'admin',
            component: AdminComponent,
            children: [
              {
                path: 'repositories',
                component: RepositoryAdminComponent,
              },
              {
                path: 'genericLoadDrivers',
                component: GenericLoadDriversAdminComponent,
              },
              {
                path: 'deploymentTypes',
                component: DeploymentTypesAdminComponent,
              },
              {
                path: 'referenceDeployments',
                component: ReferenceDeploymentsAdminComponent,
              },
              {
                path: 'deploymentDependantLoadDrivers',
                component: DeploymentDependantLoadDriversComponent,
              },
              {
                path: 'fileSystems',
                component: FileSystemsAdminComponent,
              },
              {
                path: 'userRoles',
                component: UserRolesAdminComponent,
              }

            ]
          }

        ]
      }

    ]
  },
  {
    // Old path from previous version
    path: 'reviewtool-web',
    redirectTo: ''
  },
  {
    path: 'reviewtool-web/index.jsf',
    redirectTo: ''
  },
  {
    path: 'readyReckoner',
    component: ReadyReckonerAppComponent,
    children: [
      {
        path: '',
        component: NetworkElementSearchFormComponent,
        canDeactivate: [CanDeactivateGuard]
      },
      {
        path: 'catalog',
        component: CatalogComponent
      },
      {
        path: 'results',
        component: ReferenceDeploymentSizesComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
