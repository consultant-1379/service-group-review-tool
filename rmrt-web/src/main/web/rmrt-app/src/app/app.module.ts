import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule} from "@angular/common/http";

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { RepositoriesService } from "./rmrt-app/services/repositories/repositories.service";
import { AdminComponent } from './rmrt-app/admin/admin.component';
import { RepositoriesViewComponent } from './rmrt-app/views/repositories-view/repositories-view.component';
import { RepositoryViewComponent } from './rmrt-app/views/repositories-view/repository-view/repository-view.component';
import { GenericLoadDriversViewComponent } from './rmrt-app/views/generic-load-drivers-view/generic-load-drivers-view.component';
import { ReferenceDeploymentsViewComponent } from './rmrt-app/views/reference-deployments-view/reference-deployments-view.component';
import { FileSystemsViewComponent } from './rmrt-app/views/file-systems-view/file-systems-view.component';
import { KeyDimensioningValuesViewComponent } from './rmrt-app/views/key-dimensioning-values-view/key-dimensioning-values-view.component';
import { ResourceModelViewComponent } from './rmrt-app/views/repositories-view/repository-view/resourceModel/resource-model-view/resource-model-view.component';
import { ParametersViewComponent } from './rmrt-app/views/repositories-view/repository-view/resourceModel/parameters-view/parameters-view.component';
import { LoadDriversViewComponent } from './rmrt-app/views/repositories-view/repository-view/resourceModel/load-drivers-view/load-drivers-view.component';
import { DeploymentDependenciesViewComponent } from './rmrt-app/views/repositories-view/repository-view/resourceModel/deployment-dependencies-view/deployment-dependencies-view.component';
import { ScaleUnitsViewComponent } from './rmrt-app/views/repositories-view/repository-view/resourceModel/scale-units-view/scale-units-view.component';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { DeploymentSizeSelectorComponent } from './rmrt-app/deployment-size-selector/deployment-size-selector.component';
import { FormulaErrorTooltipComponent } from './rmrt-app/views/repositories-view/repository-view/resourceModel/scale-units-view/formula-error-tooltip/formula-error-tooltip.component';
import { HomeViewComponent } from './rmrt-app/views/home-view/home-view.component';
import { UnitViewComponent } from './rmrt-app/views/repositories-view/repository-view/resourceModel/scale-units-view/unit-view/unit-view.component';
import { UnitFormulaViewComponent } from './rmrt-app/views/repositories-view/repository-view/resourceModel/scale-units-view/unit-formula-view/unit-formula-view.component';
import { RepositoryAdminComponent } from './rmrt-app/admin/repository-admin/repository-admin.component';
import { GenericLoadDriversAdminComponent } from './rmrt-app/admin/generic-load-drivers-admin/generic-load-drivers-admin.component';
import { ReferenceDeploymentsAdminComponent } from './rmrt-app/admin/reference-deployments-admin/reference-deployments-admin.component';
import { FileSystemsAdminComponent } from './rmrt-app/admin/file-systems-admin/file-systems-admin.component';
import { AdminLoginComponent } from './rmrt-app/views/admin-login/admin-login.component';
import { DeploymentDependantLoadDriversComponent } from './rmrt-app/admin/deployment-dependant-load-drivers/deployment-dependant-load-drivers.component';
import { SearchDialogComponent } from './rmrt-app/dialogs/search-dialog/search-dialog.component';
import { FileUploadComponent } from './rmrt-app/dialogs/file-upload/file-upload.component';
import { UserRolesAdminComponent } from './rmrt-app/admin/user-roles-admin/user-roles-admin.component';
import { RmrtAppComponent } from './rmrt-app/rmrt-app.component';
import { ReadyReckonerAppComponent } from './ready-reckoner-app/ready-reckoner-app.component';
import { NetworkElementSearchFormComponent } from './ready-reckoner-app/components/views/network-element-search-form/network-element-search-form.component';
import { NetworkSelectComponent } from './ready-reckoner-app/components/views/network-select/network-select.component';
import { CatalogComponent } from './ready-reckoner-app/components/views/catalog-view/catalog.component';
import { ReferenceDeploymentSizesComponent } from './ready-reckoner-app/components/views/reference-deployment-sizes/reference-deployment-sizes.component';
import { DeploymentTypesAdminComponent } from './rmrt-app/admin/deployment-types-admin/deployment-types-admin.component';
import { LoadingResultsProgressComponent } from './ready-reckoner-app/components/views/loading-results-progress/loading-results-progress.component';
import { ExpandedAccordionRowComponent } from './ready-reckoner-app/components/views/expanded-accordion-row/expanded-accordion-row.component';
import { NewLoadDriversDialogComponent } from './rmrt-app/dialogs/new-load-drivers-dialog/new-load-drivers-dialog.component';
import { NoCommaPipe } from './ready-reckoner-app/pipes/no-comma.pipe';
import {PreviousRouteService} from "./ready-reckoner-app/services/previousRoute/previous-route.service";

@NgModule({
  declarations: [
    AppComponent,
    AdminComponent,
    RepositoriesViewComponent,
    RepositoryViewComponent,
    GenericLoadDriversViewComponent,
    ReferenceDeploymentsViewComponent,
    FileSystemsViewComponent,
    KeyDimensioningValuesViewComponent,
    ResourceModelViewComponent,
    ParametersViewComponent,
    LoadDriversViewComponent,
    DeploymentDependenciesViewComponent,
    ScaleUnitsViewComponent,
    DeploymentSizeSelectorComponent,
    FormulaErrorTooltipComponent,
    HomeViewComponent,
    UnitViewComponent,
    UnitFormulaViewComponent,
    RepositoryAdminComponent,
    GenericLoadDriversAdminComponent,
    ReferenceDeploymentsAdminComponent,
    FileSystemsAdminComponent,
    AdminLoginComponent,
    DeploymentDependantLoadDriversComponent,
    SearchDialogComponent,
    FileUploadComponent,
    UserRolesAdminComponent,
    RmrtAppComponent,
    ReadyReckonerAppComponent,
    NetworkElementSearchFormComponent,
    NetworkSelectComponent,
    CatalogComponent,
    ReferenceDeploymentSizesComponent,
    DeploymentTypesAdminComponent,
    LoadingResultsProgressComponent,
    ExpandedAccordionRowComponent,
    NewLoadDriversDialogComponent,
    NoCommaPipe
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [RepositoriesService, PreviousRouteService],
  bootstrap: [AppComponent]
})
export class AppModule { }
