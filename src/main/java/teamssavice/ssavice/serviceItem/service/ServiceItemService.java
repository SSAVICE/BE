package teamssavice.ssavice.serviceItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.service.CompanyReadService;
import teamssavice.ssavice.global.dto.CursorResult;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.service.ImageReadService;
import teamssavice.ssavice.imageresource.service.ImageWriteService;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceItemService {
    private final CompanyReadService companyReadService;
    private final ServiceItemWriteService serviceItemWriteService;
    private final ServiceItemReadService serviceItemReadService;
    private final ImageReadService imageReadService;
    private final ImageWriteService imageWriteService;
    private final S3Service s3Service;

    @Transactional
    public Long register(ServiceItemCommand.Create command) {
        Company company = companyReadService.findById(command.companyId());
        List<ImageResource> imageResourceList = imageReadService.findAllByObjectKeyIn(command.imageObjectKeys());
        List<Long> imageIds = imageResourceList.stream().map(ImageResource::getId).toList();
        ServiceItem savedServiceItem = serviceItemWriteService.save(command, imageIds, company);
        imageWriteService.activeImages(imageResourceList);

        // 이거 분리해야됨
        for (ImageResource imageResource : imageResourceList) {
            s3Service.updateIsActiveTag(imageResource.getObjectKey(), true);
        }

        return savedServiceItem.getId();
    }

    @Transactional(readOnly = true)
    public CursorResult<ServiceItemModel.ItemInfo> search(ServiceItemCommand.Search command) {

        Slice<ServiceItem> items = serviceItemReadService.search(command);

        List<ServiceItemModel.ItemInfo> content = items.getContent().stream()
                .map(ServiceItemModel.ItemInfo::from)
                .toList();

        Long nextCursor = null;
        if (!content.isEmpty()) {
            nextCursor = content.getLast().serviceId();
        }

        return new CursorResult<>(content, nextCursor, items.hasNext());
    }

    @Transactional(readOnly = true)
    public ServiceItemModel.ItemInfo getServiceDetail(Long serviceId) {

        ServiceItem serviceItem = serviceItemReadService.findById(serviceId);

        return ServiceItemModel.ItemInfo.from(serviceItem);
    }
}
